import axios from "axios";
import {Spin, App} from "antd";
import {useEffect, useState} from "react";
import {getRes} from "./utils/constants";
import IndexLayout from "./components";
import {resLoadedBySsr, setRes} from "./index";

axios.defaults.baseURL = document.baseURI;

type AppState = {
    resLoaded: boolean;
};

const AppBase = () => {
    const [appState, setAppState] = useState<AppState>({resLoaded: resLoadedBySsr});

    const {modal} = App.useApp();

    const loadResourceFromServer = () => {
        const resourceApi = "/api/install/installResource";
        axios
            .get(resourceApi)
            .then(({data}: { data: Record<string, any> }) => {
                setRes(data.data)
                setAppState({resLoaded: true})
            })
            .catch((error: any) => {
                modal.error({
                    title: "Load error",
                    content: (
                        <div style={{paddingTop: 20}} dangerouslySetInnerHTML={{__html: error.response.data}}/>
                    ),
                    okText: "确认",
                });
            });
    };

    const initRes = () => {
        const resourceData = getRes();
        if (Object.keys(resourceData).length !== 0) {
            return;
        }
        loadResourceFromServer();
    };


    useEffect(() => {
        initRes();
    }, []);

    if (!appState.resLoaded) {
        return <Spin fullscreen delay={1000}/>;
    }

    return (
        <IndexLayout/>
    );
};

export default AppBase;
