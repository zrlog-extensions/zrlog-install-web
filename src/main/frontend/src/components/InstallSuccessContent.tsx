import {App, Button, Card, Typography} from "antd"
import Title from "antd/es/typography/Title"
import axios from "axios"
import {marked} from "marked"
import {getRes} from "utils/constants"

const InstallSuccessContent = ({content, askConfig}: { content: string, askConfig: boolean }) => {

    const {message} = App.useApp();


    const getOkBtn = () => {
        if (askConfig) {
            return <Button onClick={() => {
                axios.get("/api/install/installResource").then(({data}) => {
                    if (data.data.missingConfig) {
                        message.error(data.data.missingConfigTips);
                    }
                })
            }} size={"large"} type={"link"}>{getRes()['askConfigTips']}</Button>
        }
        return <Button href={document.baseURI} size={"large"} type={"link"}>{getRes().installSuccessView}</Button>
    }

    return <div style={{textAlign: 'center'}}>
        <Title level={3} type='success'>{getRes().installSuccess}</Title>
        {(content && content.length > 0) && (
            <Card>
                <Typography
                    style={{textAlign: "left"}}
                    dangerouslySetInnerHTML={{__html: marked(content) as string}}/>
            </Card>)}
        {getOkBtn()}
    </div>
}

export default InstallSuccessContent