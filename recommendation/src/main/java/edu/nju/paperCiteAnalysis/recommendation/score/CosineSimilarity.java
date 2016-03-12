package edu.nju.paperCiteAnalysis.recommendation.score;

/**
 * Created by zxy on 16-3-12.
 */
public class CosineSimilarity {

    public double cosineSimilarity(double[] vector1, double[] vector2){
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for(int i = 0; i < vector1.length; i++){
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i],2);
            norm2 += Math.pow(vector2[i],2);
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        if(norm1 != 0.0 && norm2 != 0.0){
            return dotProduct / (norm1 * norm2);
        }else{
            return 0;
        }

    }
}
