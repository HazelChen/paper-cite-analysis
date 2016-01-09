package nju.edu.classifiy.aparefine;

import edu.nju.classifier.common.HBaseConstant;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nathan on 16-1-6.
 */
public class ApaMapper extends TableMapper<Text, Text> {
    public void map(ImmutableBytesWritable row, Result value, Context context)
            throws InterruptedException, IOException {

        Text apa = getValue(value, HBaseConstant.REGION_NAME, HBaseConstant.APA);
        Text result = new Text(getResult(apa.toString()));

        context.write(new Text(row.toString()), result);

    }

    private Text getValue(Result value, String region, String column) {
        return new Text(
                new String(
                        value.getValue(Bytes.toBytes(region),
                                Bytes.toBytes(column))));
    }

    public String getResult(String apa) {
        ArrayList<String> items = new ArrayList<String>();
        int start=0, count=0;
        for(int i=0; i<apa.length(); ++i) {
            if(count == 3) {
                items.add(apa.substring(start, apa.length() - 1));
                break;
            }
            if(apa.charAt(i) == '.') {
                if(i+1<apa.length()) {
                    if(apa.charAt(i+1) == ' ') {
                        items.add(apa.substring(start, i));
                        start = i + 2;
                        count++;
                    }
                }else{
                    items.add(apa.substring(start, i));
                    start = i + 2;
                    count++;
                }
            }else
                continue;
        }
        return convert2String(items);
    }

    private String convert2String(ArrayList<String> items) {
        if(items.size() != 4 && items.size() != 3)   {
            //return "OMG!!!!!!!!!!!!!!!\r\n" + items.toString() + "\r\n";
            return "";
        }

        String tmp = ",";
        if(items.size() == 4) {
            tmp = items.get(3);
        }

        StringBuffer sb = new StringBuffer(256);
        String[] publicationData = tmp.split(",");
        if(publicationData.length <= 1) {
            sb.append("type:inproceedings");
        } else {
            sb.append("type:article");
        }

        sb.append("\r\n");
        sb.append("title:" + items.get(2));
        sb.append("\r\n");
        sb.append("author:" + items.get(0));
        sb.append("\r\n");
        sb.append("year:" + items.get(1).substring(1,items.get(1).length()-1));
        sb.append("\r\n");

        if(publicationData.length == 1) {
            sb.append("booktitle:" + publicationData[0]);
            sb.append("\r\n");
        }else if(publicationData.length == 2) {
            sb.append("journal:" + publicationData[0]);
            sb.append("\r\n");
            sb.append("volume:" + publicationData[1]);
            sb.append("\r\n");
        }else if(publicationData.length == 3) {
            sb.append("journal:" + publicationData[0]);
            sb.append("\r\n");
            sb.append("volume:" + publicationData[1]);
            sb.append("\r\n");
            sb.append("pages:" + publicationData[2]);
            sb.append("\r\n");
        }

        return sb.toString();
    }

}
