import React from 'react';
import {Card, Typography} from "antd";
import {getRes} from "../utils/constants";
import {marked} from 'marked';

const DisclaimerAgreement: React.FC = () => {
    return (
        <Card styles={{body: {maxHeight: 400, overflowY: "auto", paddingTop: 0}}}>
            <Typography dangerouslySetInnerHTML={{__html: marked(getRes()['disclaimerAgreement']) as string}}/>
        </Card>
    );
};


export default DisclaimerAgreement;