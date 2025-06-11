function fmtDateDay(val, obj, row, act,fmt){
    if (val==null || val==NaN || val==undefined || val=="") return "";
    return js.formatDate(new Date(val),"yyyy-MM-dd");
}

function fmtDateDayHHmm(val, obj, row, act,fmt){
    if (val==null || val==NaN || val==undefined || val=="") return "";
    return js.formatDate(new Date(val),"yyyy-MM-dd HH:mm");
}

function fmtDateDayHourMinFull(val, obj, row, act,fmt){
    if (val==null || val==NaN || val==undefined || val=="") return "";
    return js.formatDate(new Date(val),"yyyy-MM-dd HH:mm:ss");
}

function fmtDate(date,fmt){
    return js.formatDate(date,fmt);
}

function formatProcedureProcessAction(actionKey,jsonString) {
    if (actionKey==null || actionKey=='') return jsonString;
    if (jsonString==null || jsonString=='') return '';
    let json=JSON.parse(jsonString);
    let valueList=[];
    switch (actionKey) {
        case 'dye':
            valueList.push('染料：'+json.processActionDAObjectTitle);
            valueList.push('剂量：'+json.processActionDAObjectValue+' '+json.processActionDAObjectUnit);
            break;
        case 'adjuvant':
            valueList.push('助剂：'+json.processActionDAObjectTitle);
            valueList.push('剂量：'+json.processActionDAObjectValue+' '+json.processActionDAObjectUnit);
            break;
        case 'temperature':
            valueList.push('温度：'+getTemperatureAction(json.processActionTemperature));
            valueList.push('度数：'+json.processActionTemperatureValue+" ℃");
            valueList.push('间隔：'+json.processActionTemperatureInterrupt+' 分钟');
            valueList.push('时长：'+json.processActionTemperatureTime+' 分钟');
            break;
        case 'water':
            valueList.push('水位控制：'+getWaterAction(json.processActionWater));
            valueList.push('水位：'+json.processActionWaterValue+' '+json.processActionWaterUnit);
            valueList.push('间隔：'+json.processActionWaterInterrupt+' 分钟');
            valueList.push('时长：'+json.processActionWaterTime+' 分钟');
            break;
        default:
            return jsonString;
    }
    return valueList.join('<br />');
}

function getWaterAction(val) {
    if(val==undefined || val==null) return '';
    switch (val) {
        case 'increase':
            return '增加';
        case 'reduce':
            return '降低';
        case 'keep':
            return '保持';
        default:
            return '';
    }
}

function getTemperatureAction(val) {
    if(val==undefined || val==null) return '';
    switch (val) {
        case 'increase':
            return '增加';
        case 'reduce':
            return '降低';
        case 'keep':
            return '保持';
        default:
            return '';
    }
}

function getTextValue(val) {
    if (val==null || val==NaN || val==undefined || val=="") return "";
    return val;
}

function fmtYNValue(val, obj, row, act,fmt){
    if (val==null || val==NaN || val==undefined || val=="") return "";
    val=val.toLowerCase();
    switch (val) {
        case 'y':
            return '是';
        case 'n':
            return '否';
        default:
            return '';
    }
}

function fmtStoreTypeValue(val, obj, row, act,fmt){
    if (val==null || val==NaN || val==undefined || val=="") return "";
    val=val.toLowerCase();
    switch (val) {
        case 's':
            return '一般性仓库';
        case 'w':
            return '在制品仓库';
        default:
            return '';
    }
}