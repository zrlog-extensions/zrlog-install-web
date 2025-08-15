import {Button, Card, Typography} from "antd"
import Title from "antd/es/typography/Title"
import {marked} from "marked"
import {getRes} from "utils/constants"

const InstallSuccessContent = ({content}: { content: string }) => {

    return <div style={{textAlign: 'center'}}>
        <Title level={3} type='success'>{getRes().installSuccess}</Title>
        {(content && content.length > 0) && (
            <Card>
                <Typography
                    style={{textAlign: "left"}}
                    dangerouslySetInnerHTML={{__html: marked(content) as string}}/>
            </Card>)}
        <Button href={document.baseURI} size={"large"} type={"link"}>{getRes().installSuccessView}</Button>
    </div>
}

export default InstallSuccessContent