package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import filters.UserAuth;
import modules.SysParCom;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.client;
import static play.libs.Json.toJson;

/**
 * Created by sibyl.sun on 16/3/18.
 */
public class ComCtrl extends Controller {

    /**
     * 图片json串转图片url
     * @param imgJson
     * @return
     */
    public String getImgUrl(String imgJson){
        if (imgJson.contains("url")) {
            JsonNode jsonNode = Json.parse(imgJson);
            if (jsonNode.has("url")) {
                return jsonNode.get("url").asText();
            }
        }
        return SysParCom.IMAGE_URL + imgJson;
    }

    /**
     * 获取商品详情的URL
     * @param skuType
     * @param itemId
     * @param skuTypeId
     * @return
     */
    public String getSkuDetailUrl(String skuType,Long itemId,Long skuTypeId){
        StringBuffer sb=new StringBuffer();
        sb.append("/detail/").append(skuType);
        if(itemId>0) {
            sb.append("/").append(itemId);
        }
        if(skuTypeId>0){
            sb.append("/").append(skuTypeId);
        }
        return sb.toString();
    }

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> getReqReturnMsg(String url){
        return sendReq(url,null);

    }
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> postReqReturnMsg(String url,RequestBody requestBody){
        return sendReq(url,requestBody);

    }
    @Security.Authenticated(UserAuth.class)
    private F.Promise<Result> sendReq(String url,RequestBody requestBody){
        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request =null;
            if(null==requestBody){
                request= builder.url(url).get().build();
            }else{
                request= builder.url(url).post(requestBody).build();
            }
            Logger.error(url+"======client========"+client);
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误json=" + json);
                return badRequest();
            }
            return ok(toJson(message));
        } );
    }

}
