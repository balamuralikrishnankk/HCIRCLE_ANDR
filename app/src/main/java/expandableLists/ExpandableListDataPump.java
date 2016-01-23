package expandableLists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> sublist1 = new ArrayList<String>();
        sublist1.add("Laboratory Group");
        sublist1.add("Cardiology Dept.");

        List<String> sublist2 = new ArrayList<String>();
        sublist2.add("Laboratory Group");
        sublist2.add("Cardiology Dept.");
        sublist2.add("ENT");

        List<String> sublist3 = new ArrayList<String>();
        sublist3.add("Laboratory Group");
        sublist3.add("Cardiology Dept.");
        sublist3.add("ENT");

        List<String> sublist4 = new ArrayList<String>();
        sublist4.add("Laboratory Group");
        sublist4.add("Cardiology Dept.");
        sublist4.add("ENT");

        expandableListDetail.put("Hiranandani Hospital", sublist1);
        expandableListDetail.put("Lilavati Hospital", sublist2);
        expandableListDetail.put("RIMS", sublist3);
        expandableListDetail.put("J.N. Hospital", sublist4);
        return expandableListDetail;
    }
}
