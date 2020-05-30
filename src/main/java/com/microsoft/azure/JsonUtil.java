package com.microsoft.azure;

import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonUtil {

    private static String topLevelPropTemplate="$.$v.%s.$v";

    public static <T> T getTopLevelProp(String json,String propName)
    {
        return JsonPath.read(json,String.format(topLevelPropTemplate,propName));
    }

}
