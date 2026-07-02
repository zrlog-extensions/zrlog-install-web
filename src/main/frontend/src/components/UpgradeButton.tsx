import {App, Button, Space, Typography} from "antd";
import Alert from "antd/es/alert";
import {marked} from "marked";
import {formatText, getRes} from "utils/constants";

const UpgradeButton = () => {

    const {modal} = App.useApp()


    const res = getRes();
    const upgradeVersion = res.upgradeVersion;

    if (!upgradeVersion) {
        return <></>
    }

    return <Alert type='info'
                  action={
                      <Space.Compact>
                          <Button size={"small"} type="default" onClick={() => {
                              modal.info({
                                  width: 682,
                                  title: res.upgrade.newVersion,
                                  content: <Typography
                                      dangerouslySetInnerHTML={{__html: marked(res.upgradeChangeLog || "") as string}}/>,
                              })
                          }}>
                              {res.common.detail}
                          </Button>
                          <Button size={"small"} type="primary" href={res.upgradeDownloadUrl}>
                              {res.common.download}
                          </Button>
                      </Space.Compact>
                  }
                  message={<div
                      dangerouslySetInnerHTML={{__html: formatText(res.upgrade.newVersionTip, {version: upgradeVersion})}}/>}
                  showIcon/>
}

export default UpgradeButton;
