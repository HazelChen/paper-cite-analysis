package edu.nju.paperCiteAnalysis.recommendation.front;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hazel on 2016-03-12.
 */
@Path("/recommend-service")
public class RecommendFrontService {
    private Gson gson;
    private RecommendController controller;

    public RecommendFrontService() {
        initGson();

        controller = new RecommendControllerStub();
    }

    @Path("/recommend")
    @GET
    public String recommend(@DefaultValue("") @QueryParam("input") String input) {

//        List<String> inputBibtexs = gson.fromJson(input,
//                new TypeToken<List<String>>() {}.getType());
        List<String> inputBibtexs = new ArrayList<String>();
        Map<Bibtex, Integer> bibtexWithGoal = controller.recommend(inputBibtexs);
        return gson.toJson(bibtexWithGoal);
    }

    @Path("/like")
    @POST
    public String like(@DefaultValue("") @QueryParam("input") String inputJson) {
        Bibtex bibtex = gson.fromJson(inputJson, Bibtex.class);
        return "";
    }

    @Path("/dislike")
    @POST
    public String dislike(@DefaultValue("") @QueryParam("input") String inputJson) {
        Bibtex bibtex = gson.fromJson(inputJson, Bibtex.class);
        return "";
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }


}
