import React from 'react';
import {Card, Typography} from "antd";
import {getRes} from "../utils/constants";

const DisclaimerAgreement: React.FC = () => {
    return (
        <Card bodyStyle={{maxHeight: 400, overflowY: "auto", paddingTop: 0}}>
            <Typography dangerouslySetInnerHTML={{__html: getRes()['disclaimerAgreement']}}>
            </Typography>
        </Card>
    );
};


export default DisclaimerAgreement;