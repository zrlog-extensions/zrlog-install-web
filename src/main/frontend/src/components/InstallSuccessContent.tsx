import {Button, Card, message, Typography} from "antd"
import Title from "antd/es/typography/Title"
import axios from "axios"
import {marked} from "marked"
import {getRes} from "utils/constants"

const InstallSuccessContent = ({content, askConfig}: {
    content: string,
    askConfig: boolean,
}) => {

    const [messageApi, contextHolder] = message.useMessage({maxCount: 3});

    if (!askConfig && getRes().installed) {
        return <></>
    }

    const getOkBtn = () => {
        if (askConfig) {
            return <Button onClick={() => {
                axios.get("/api/install/installResource").then(({data}) => {
                    if (data.data.askConfig) {
                        messageApi.error(getRes().installedPage.missingConfigTips);
                    } else {
                        window.location.href = document.baseURI;
                    }
                })
            }} size={"large"} type={"link"}>{getRes().installedPage.askConfigTips}</Button>
        }
        return <Button href={document.baseURI} size={"large"} type={"link"}>{getRes().success.viewSite}</Button>
    }


    return <>
        {contextHolder}
        {!askConfig && <Title level={3} type='success'>{getRes().success.installSuccess}</Title>}
        {(content && content.length > 0) && (
            <Card style={{
                marginTop: 16,
                marginBottom: 16,
                width: "100%",
                maxWidth: 960,
                textAlign: "left",
            }} styles={{
                body: {
                    paddingTop: 0,
                }
            }} title={askConfig ? getRes().success.askConfigTitle : getRes().installedPage.title}>
                <Typography
                    style={{textAlign: "left"}}
                    dangerouslySetInnerHTML={{__html: marked(content) as string}}/>
            </Card>)}
            {getOkBtn()}
    </>
}

export default InstallSuccessContent
