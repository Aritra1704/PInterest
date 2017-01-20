package com.arpaul.pinterest.dataobject;

import java.util.LinkedHashMap;

/**
 * Created by Aritra on 15-08-2016.
 */
public class ParserDO extends BaseDO {

    public LinkedHashMap<ParseType, Object> linkHash = new LinkedHashMap<>();

    public enum ParseType {
        TYPE_LIST
    }
}
