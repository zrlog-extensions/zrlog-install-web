import zh_CN from "antd/es/locale/zh_CN";
import en_US from "antd/es/locale/en_US";
import {App, ConfigProvider, theme} from "antd";
import AppBase from "./AppBase";
import {useEffect, useState} from "react";
import EnvUtils from "./utils/env-utils";
import {BrowserRouter} from "react-router-dom";
import {legacyLogicalPropertiesTransformer, StyleProvider} from "@ant-design/cssinjs";
import {createRoot} from "react-dom/client";
import {getRes, resourceKey} from "./utils/constants";

const {darkAlgorithm, defaultAlgorithm} = theme;

const jsonStr = document.getElementById("resourceInfo")?.textContent;
export let resLoadedBySsr = false;

export const setRes = (data: Record<string, unknown>) => {
    data.copyrightTips =
        data.copyright + ' <a target="_blank" href="https://blog.zrlog.com/about.html?footer">ZrLog</a>';
    //@ts-ignore
    window[resourceKey] = JSON.stringify(data);
}

let lang = "zh_CN";

if (jsonStr && jsonStr !== "") {
    setRes(JSON.parse(jsonStr));
    lang = getRes()['lang']
    resLoadedBySsr = true;
}

const Index = () => {
    const [dark, setDark] = useState<boolean>(EnvUtils.isDarkMode);

    useEffect(() => {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        const changeHandler = () => setDark(EnvUtils.isDarkMode());

        mediaQuery.addEventListener('change', changeHandler);

        // 在组件卸载时移除事件监听器
        return () => mediaQuery.removeEventListener('change', changeHandler);
    }, []);

    return (
        <ConfigProvider
            locale={lang.startsWith("zh") ? zh_CN : en_US}
            theme={{
                algorithm: dark ? darkAlgorithm : defaultAlgorithm,
            }}
        >
            <App>
                <BrowserRouter>
                    <StyleProvider transformers={[legacyLogicalPropertiesTransformer]}>
                        <AppBase/>
                    </StyleProvider>
                </BrowserRouter>
            </App>
        </ConfigProvider>
    );
};

const container = document.getElementById("app");
const root = createRoot(container!); // createRoot(container!) if you use TypeScript
root.render(<Index/>);
