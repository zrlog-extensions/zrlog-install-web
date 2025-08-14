import {App, Button, Divider, Space, Typography} from "antd";
import Alert from "antd/es/alert";
import {marked} from "marked";
import {getRes} from "utils/constants";

const UpgradeButtion = () => {

    const {modal} = App.useApp()


    const tips = getRes()['upgradeTips'];

    if (tips === "") {
        return <></>
    }

    return <div>
        <Alert type='info'
               action={
                   <Space.Compact>
                       <Button size={"middle"} type="default" onClick={() => {
                           modal.info({
                               width: 682,
                               title: getRes()['newVersion'],
                               content: <Typography
                                   dangerouslySetInnerHTML={{__html: marked(getRes()['upgradeChangeLog']) as string}}/>,
                           })
                       }}>
                           {getRes()['detail']}
                       </Button>
                       <Button size={"middle"} type="primary" href={getRes()['upgradeDownloadUrl']}>
                           {getRes()['download']}
                       </Button>
                   </Space.Compact>
               }
               message={<div
                   dangerouslySetInnerHTML={{__html: getRes()['upgradeTips']}}/>}
               showIcon/>
        <Divider/>
    </div>
}

export default UpgradeButtion;