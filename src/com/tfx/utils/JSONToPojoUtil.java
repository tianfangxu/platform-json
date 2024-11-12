package com.tfx.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tfx.mod.Node;
import com.tfx.mod.Pojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tianfx
 * @date 2024/11/4 10:46
 */
public class JSONToPojoUtil {

    public static String toJsonString(String json) {
        if (json == null || json.length() == 0){
            return null;
        }
        json = json.trim();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }
    
    public static Pojo getPojoObj(String json) {
        if (json == null || json.length() == 0){
            return null;
        }
        Gson gson = new Gson();
        
        json = json.trim();
        List<Map> jsonObjects = new ArrayList<>();
        if (json.startsWith("[")) {
            List array = gson.fromJson(json, List.class);
            if (array.size() == 0){
                return null;
            }
            array.forEach(t->jsonObjects.add((Map) t));
        }else if (json.startsWith("{")){
            jsonObjects.add(gson.fromJson(json,Map.class));
        }else{
            throw new IllegalArgumentException("need json type!");
        }
        Pojo pojo = new Pojo(childName(), new ArrayList<>());
        megerJsonPojo(jsonObjects,pojo);
        return pojo;
    }
    
    public static void megerJsonPojo(List<Map> jsons,Pojo pojo){
        if (jsons == null || jsons.size() == 0) {
            return;
        }
        Set<String> keySet = new HashSet<>();
        for (Map json : jsons) {
            keySet.addAll(json.keySet());
        }
        for (String key : keySet) {
            List<Object> valList = new ArrayList<>();
            for (Map json : jsons) {
                Object val = json.get(key);
                if (val == null){
                    continue;
                }
                valList.add(val);
            }
            if (valList.size() == 0){
                continue;
            }
            Object val = valList.get(0);
            if (val instanceof List){
                List<Map> keyVals = new ArrayList<>();
                for (Object obj : valList) {
                    if (!(obj instanceof List)){
                        throw new IllegalArgumentException("key = ["+ key +"] has Different value type.");
                    }
                    ((List) obj).forEach(t->keyVals.add((Map)t));
                }
                Pojo cc = new Pojo(childName(), new ArrayList<>());
                pojo.getFields().add(new Node(cc.getName(),keyRule(key),"List<%s>",cc));
                megerJsonPojo(keyVals,cc);
            }else if (val instanceof Map){
                List<Map> keyVals = new ArrayList<>();
                for (Object obj : valList) {
                    if (!(obj instanceof Map)){
                        throw new IllegalArgumentException("key = ["+ key +"] has Different value type.");
                    }
                    keyVals.add((Map) obj);
                }
                Pojo cc = new Pojo(childName(), new ArrayList<>());
                pojo.getFields().add(new Node(cc.getName(),keyRule(key),null,cc));
                megerJsonPojo(keyVals,cc);
            }else{
                pojo.getFields().add(new Node(val.getClass().getSimpleName(),keyRule(key),null,null));
                
            }
        }
        
    }
    
    public static String keyRule(String key){
        if (key == null || key.length() == 0) {
            return key;
        }
        List<Character> arr = new ArrayList<>();
        boolean split = false;
        for (char c : key.toCharArray()) {
            if ( c == 95 || c == 45){
                split = true;
            }else{
                arr.add((split && c > 96 && c < 123)? (char) (c - 32):c);
                split = false;
            }
        }
        char[] cs = new char[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            cs[i] = arr.get(i);
        }
        return new String(cs);
    }
    
    public static String childName(){
        char[] chars = new char[6];
        chars[0] = (char)(Math.random()*26 + 65);
        for (int i = 1; i < chars.length; i++) {
            chars[i] = (char)(Math.random()*26 + 97);
        }
        return new String(chars);
    }
    
    public static String writeClass(Pojo pojo){
        StringBuilder builder = new StringBuilder();
        boolean isList = false;
        for (Node field : pojo.getFields()) {
            if (field.getCompleteType().contains("List")) {
                isList = true;
            }
        }
        if (isList) {
            builder.append("import java.util.List;\n\n");
        }
        builder.append("public class ").append(pojo.getName()).append("{\n\n");
        for (Node field : pojo.getFields()) {
            builder.append("    ").append(field.getPre()).append(" ").append(field.getCompleteType()).append(" ").append(field.getKey()).append(";\n\n");
        }
        for (Node field : pojo.getFields()) {
            String fieldName = field.getKey();
            String fieldType = field.getCompleteType();
            // 生成 Getter 方法
            String getterText = "    public " + fieldType + " get" + capitalize(fieldName) + "() {\n" +
                    "    return " + fieldName + ";\n" +
                    "}\n\n";
            builder.append(getterText);
            // 生成 Setter 方法
            String setterText = "    public void set" + capitalize(fieldName) + "(" + fieldType + " " + fieldName + ") {\n" +
                    "    this." + fieldName + " = " + fieldName + ";\n" +
                    "}\n\n";
            builder.append(setterText);
        }
        builder.append("}");
        return builder.toString();
    }


    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static void main(String[] args) {
        String json = "{  \"code\": 200,  \"message\": \"ok\",  \"status\": true,  \"user\": null,  \"data\": {    \"empId\": \"OIUJKT\",    \"empName\": \"张三\",    \"memberId\": \"ILKYUX\",    \"memberName\": \"社保科技\",    \"costId\": \"PLKHXJ\",    \"costName\": \"社保科技北京分公司\",    \"orderList\": [      {        \"areaId\": \"Y6G2H7\",        \"areaName\": \"上海\",        \"insOrgId\": \"OI8WEU\",        \"insOrgName\": \"上海社保\",        \"insType\": 1,        \"insCode\": \"30\",        \"insName\": \"养老保险\",        \"accountStatus\": 1,        \"feeMonth\": 202209,        \"orgBase\": 3000,        \"orgFee\": 600,        \"orgProp\": 20,        \"empBase\": 3000,        \"empFee\": 300,        \"empProp\": 10,        \"overdueFee\": 0,        \"totalFee\": 900      },      {        \"areaId\": \"Y6G2H7\",        \"areaName\": \"上海\",        \"insOrgId\": \"OI8WEU\",        \"insOrgName\": \"上海社保\",        \"insType\": 1,        \"insCode\": \"40\",        \"insName\": \"医疗保险\",        \"accountStatus\": 1,        \"feeMonth\": 202209,        \"orgBase\": 3000,        \"orgFee\": 60,        \"orgProp\": 2,        \"empBase\": 3000,        \"empFee\": 60,        \"empProp\": 2,        \"overdueFee\": 0,        \"totalFee\": 120      },      {        \"areaId\": \"Y6G2H7\",        \"areaName\": \"上海\",        \"insOrgId\": \"OI8WEB\",        \"insOrgName\": \"上海公积金\",        \"insType\": 2,        \"insCode\": \"20\",        \"insName\": \"公积金\",        \"accountStatus\": 1,        \"feeMonth\": 202209,        \"orgBase\": 3000,        \"orgFee\": 210,        \"orgProp\": 7,        \"empBase\": 3000,        \"empFee\": 210,        \"empProp\": 7,        \"overdueFee\": 0,        \"totalFee\": 420      }    ]  }}";
        System.out.println(toJsonString(json));
        //test();
    }
}
