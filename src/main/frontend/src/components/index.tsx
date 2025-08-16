import {useRef, useState} from 'react';
import {
    Alert,
    Button,
    Card,
    Divider,
    Form,
    FormInstance,
    Input,
    Layout, message,
    Result,
    Select,
    Steps,
    Typography
} from 'antd';

import axios from "axios";
import Text from "antd/es/typography/Text";
import {getRes} from "../utils/constants";
import {mapToQueryString} from "../utils/helpers";
import DisclaimerAgreement from "./DisclaimerAgreement";
import UpgradeButtion from './UpgradeButtion';
import InstallSuccessContent from './InstallSuccessContent';

const FormItem = Form.Item;
const {Title} = Typography;

const {Footer} = Layout;

const {Step} = Steps;

const formItemLayout = {
    labelCol: {
        xs: {span: 24},
        sm: {span: 6},
        md: {span: 4},
    },
    wrapperCol: {
        xs: {span: 24},
        sm: {span: 12},
        md: {span: 8},
    },
};

type AppState = {
    current: number;
    installed: boolean,
    testConnecting: boolean,
    installing: boolean
    dataBaseInfo: Record<string, string | number>,
    weblogInfo: Record<string, string | number>;
    installSuccessContent: string;
    askConfig: boolean;
}


const IndexLayout = () => {


    const getDefaultPort = (dbType: string): number => {
        if (dbType === "webapi") {
            return 443
        } else if (dbType === "mysql") {
            return 3306;
        }
        return 0;
    }

    const [state, setState] = useState<AppState>({
        current: 0,
        installed: getRes()['installed'],
        askConfig: getRes()['askConfig'],
        testConnecting: false,
        installSuccessContent: getRes()["installSuccessContent"],
        installing: false,
        dataBaseInfo: {
            dbType: "mysql",
            dbPort: getDefaultPort("mysql"),
        },
        weblogInfo: {},
    })

    const getSteps = () => {
        return [
            {
                title: getRes()['installAgreement'],
            }, {
                title: getRes()['installDatabaseInfo'],
            }, {
                title: getRes()['installWebSiteInfo']
            }, {
                title: getRes()['installComplete']
            }
        ]
    }

    const formDataBaseInfoRef = useRef<FormInstance>(null);
    const formWeblogInfoRef = useRef<FormInstance>(null);
    const [messageApi, contextHolder] = message.useMessage({maxCount: 3});

    const setDatabaseValue = (changedValues: Record<string, string | number>, allValues: Record<string, string | number>) => {
        if (formDataBaseInfoRef === undefined || formDataBaseInfoRef.current === undefined || formDataBaseInfoRef.current === null) {
            return;
        }
        //reconfiged port
        if (changedValues['dbType'] !== undefined) {
            //@ts-ignore
            const newPort = getDefaultPort(changedValues['dbType'])
            //@ts-ignore
            allValues['dbPort'] = newPort;
            formDataBaseInfoRef.current.setFieldValue("dbPort", newPort)
        }
        formDataBaseInfoRef.current.setFieldsValue(changedValues);
        setState((prevState) => {
            return {
                ...prevState,
                dataBaseInfo: allValues,
            }
        });
    }

    const setWeblogValue = (changedValues: Record<string, string | number>, allValues: Record<string, string | number>) => {
        if (formWeblogInfoRef === undefined || formWeblogInfoRef.current === undefined || formWeblogInfoRef.current === null) {
            return;
        }
        formWeblogInfoRef.current.setFieldsValue(changedValues);
        setState({
            ...state,
            weblogInfo: allValues,
        });
    }

    const next = () => {
        if (state.current === 0) {
            const current = state.current + 1;
            setState({...state, current: current});
        }
        if (state.current === 1) {
            setState({...state, testConnecting: true})
            axios.get("/api/install/testDbConn?" + mapToQueryString(state.dataBaseInfo)).then(({data}) => {
                if (!data.error) {
                    const current = state.current + 1;
                    setState({...state, current: current, testConnecting: false});
                } else {
                    messageApi.error(data.message);
                    setState({...state, testConnecting: false})
                }
            })
        } else if (state.current === 2) {
            setState({...state, installing: true})
            axios.post("/api/install/startInstall", mapToQueryString({
                ...state.dataBaseInfo,
                ...state.weblogInfo
            }), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(({data}) => {
                if (!data.error) {
                    const current = state.current + 1;
                    setState({
                        ...state,
                        current: current,
                        installing: false,
                        installSuccessContent: data.data.content
                    });
                } else {
                    messageApi.error(data.message);
                    setState({...state, installing: false})
                }
            })
        }
    }

    const prev = () => {
        const current = state.current - 1;
        setState({...state, current: current});
    }

    if (state.installed) {
        if (state.askConfig) {
            return <Layout style={{
                height: "100vh", paddingRight: 12,
                paddingLeft: 12, display: "flex", alignItems: "center"
            }}>
                <InstallSuccessContent content={state.installSuccessContent} askConfig={state.askConfig}/>
            </Layout>
        }
        return <Result status={"error"} title={getRes().installedTitle} subTitle={getRes().installedTips}
                       style={{padding: 0}}/>

    }

    const showFeedback = () => {
        if (state.current === 1 || state.current === 2) {
            return <>
                <Divider/>
                <Title level={4} style={{textAlign: "center", marginTop: '20px'}}>
                    <div dangerouslySetInnerHTML={{__html: getRes().installFeedback}}/>
                </Title>
            </>
        }
        return <></>
    }


    return (
        <Layout style={{
            height: "100vh", paddingRight: 12,
            paddingLeft: 12, display: "flex", alignItems: "center"
        }}>
            {contextHolder}
            <Card title={getRes().installWizard} style={{
                marginTop: 32, marginBottom: 32, width: "100%",
                maxWidth: 960
            }}>
                <UpgradeButtion/>
                <div hidden={getRes()['utfTips'] === ''}>
                    <Alert type='error'
                           message={<div
                               dangerouslySetInnerHTML={{__html: getRes()['utfTips']}}/>}
                           showIcon/>
                    <Divider/>
                </div>
                <Steps current={state.current}>
                    {getSteps().map(item => (
                        <Step key={item.title + ""} title={item.title + ""}/>
                    ))}
                </Steps>
                <div style={{marginTop: '20px'}}>
                    {state.current === 0 && <DisclaimerAgreement/>}
                    {state.current === 1 && (
                        <Form ref={formDataBaseInfoRef} initialValues={state.dataBaseInfo} {...formItemLayout}
                              onValuesChange={(k: any, v: any) => setDatabaseValue(k, v)}>
                            <div>
                                <Title level={3}
                                       type="danger">{getRes().installPrompt}</Title>
                                <ul>
                                    <li><Text type="danger">{getRes().installWarn1}</Text>
                                    </li>
                                    <li><Text type="danger">{getRes().installWarn2}</Text>
                                    </li>
                                    <li><Text type="danger">{getRes().installWarn3}</Text>
                                    </li>
                                </ul>
                            </div>
                            <Title level={4}>{getRes().installInputDbInfo}</Title>
                            <FormItem name='dbType' label={getRes().installDbType}
                                      rules={[{required: true}]}>
                                <Select>
                                    <Select.Option value="mysql">MySQL / MariaDB</Select.Option>
                                    <Select.Option value="webapi">WebApi</Select.Option>
                                </Select>
                            </FormItem>
                            <FormItem name='dbHost' label={getRes().installDbHost}
                                      rules={[{required: true}]}>
                                <Input placeholder='127.0.0.1'/>
                            </FormItem>
                            <FormItem name='dbName' label={getRes().installDbName}
                                      rules={[{required: true}]}>
                                <Input placeholder='ZrLog'/>
                            </FormItem>
                            <FormItem name='dbUserName' label={getRes().installDbUserName}
                                      rules={[{required: true}]}>
                                <Input placeholder=''/>
                            </FormItem>
                            <FormItem name='dbPassword' label={getRes().installDbPassword}>
                                <Input type='password'/>
                            </FormItem>
                            <FormItem name='dbPort' label={getRes().installDbPort}
                                      rules={[{required: true}]}>
                                <Input type='number'
                                       placeholder={getDefaultPort(state.dataBaseInfo.dbType as string) + ""}
                                       style={{maxWidth: 108}}/>
                            </FormItem>
                        </Form>
                    )}
                    {state.current === 2 && (
                        <Form ref={formWeblogInfoRef} {...formItemLayout}
                              onValuesChange={(k: Record<string, string | number>, v: Record<string, string | number>) => setWeblogValue(k, v)}>
                            <Title level={3}>{getRes().installInputWebSiteInfo}</Title>
                            <FormItem name='username' label={getRes().installAdmin}
                                      rules={[{required: true}]}>
                                <Input placeholder='admin'/>
                            </FormItem>
                            <FormItem name='password' label={getRes().installAdminPassword}
                                      rules={[{required: true}]}>
                                <Input type='password'/>
                            </FormItem>
                            <FormItem name='email' label={getRes().installAdminEmail}>
                                <Input type='email'/>
                            </FormItem>
                            <FormItem name='title' label={getRes().installWebSiteTitle}
                                      rules={[{required: true}]}>
                                <Input placeholder={getRes().installWebSiteTitleTip}/>
                            </FormItem>
                            <FormItem name='second_title'
                                      label={getRes().installWebSiteSecond}>
                                <Input/>
                            </FormItem>
                        </Form>
                    )}
                    {state.current === 3 && (
                        <div style={{textAlign: "center"}}>
                            <InstallSuccessContent content={state.installSuccessContent} askConfig={state.askConfig}/>
                        </div>
                    )}
                </div>
                <div style={{paddingTop: state.current <= 2 ? 20 : 0}}>
                    {state.current === 0 && (
                        <Button type="primary" onClick={() => next()}>
                            {getRes().installAgreementNext}
                        </Button>
                    )}
                    {state.current === 1 && (
                        <Button loading={state.testConnecting} type="primary" onClick={() => next()}>
                            {getRes().installNextStep}
                        </Button>
                    )}
                    {state.current === 2 && (
                        <>
                            <Button loading={state.installing} type="primary" onClick={() => next()}>
                                {getRes().installNextStep}
                            </Button>
                            <Button style={{margin: '0 8px'}} onClick={() => prev()}>
                                {getRes().installPreviousStep}
                            </Button>
                        </>
                    )}
                </div>
                {showFeedback()}
            </Card>
            <Footer style={{textAlign: 'center'}}><span
                dangerouslySetInnerHTML={{__html: getRes().copyrightTips}}/> .
                All Rights Reserved.</Footer>
        </Layout>
    );
}

export default IndexLayout;
