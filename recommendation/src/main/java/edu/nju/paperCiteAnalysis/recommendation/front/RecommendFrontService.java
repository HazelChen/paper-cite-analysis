package edu.nju.paperCiteAnalysis.recommendation.front;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hazel on 2016-03-12.
 */
@Path("/recommend-service")
public class RecommendFrontService {
    private Gson gson;
    private static RecommendController controller;

    public RecommendFrontService() {
        initGson();

        if (controller == null) {
            controller = new RecommendControllerStub();
        }
    }

    @Path("/recommend")
    @GET
    public String recommend(@DefaultValue("") @QueryParam("input") String input) {
        String[] inputs = input.split("\n");
        Map<Bibtex, Double> bibtexWithGoal = controller.recommend(Arrays.asList(inputs));
        return gson.toJson(bibtexWithGoal);
    }

    @Path("/like")
    @POST
    public String like(@DefaultValue("") @FormParam("like") String like) {
        Bibtex bibtex = getBibtexFromJson(like);
        controller.like(bibtex);
        return "";
    }

    @Path("/dislike")
    @POST
    public String dislike(@DefaultValue("") @FormParam("dislike") String dislike) {
        Bibtex bibtex = getBibtexFromJson(dislike);
        controller.dislike(bibtex);
        return "";
    }

    private Bibtex getBibtexFromJson(String json) {
        Article article = gson.fromJson(json, Article.class);
        if (article.getJournal() != null) {
            return article;
        } else {
            Inproceedings inproceedings = gson.fromJson(json, Inproceedings.class);
            return inproceedings;
        }
    }

    private void initGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.enableComplexMapKeySerialization();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }


}
