import java.util.List;

public class TestData {

    public static void main(String[] args) {
        List<String> data = FileHelper.read("/home/hazel/input/bibtex-04.txt", "utf-8");
        int i = 0;
        for (String d : data) {
            if (!d.equals("")) {
                System.out.println(i + " " + d);
                i++;
            }
        }
    }
}
