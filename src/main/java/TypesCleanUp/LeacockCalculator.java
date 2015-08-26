package TypesCleanUp;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import fragmentsWrapper.QueryWrapper;

import java.util.*;

public class LeacockCalculator {
    private Map<String, OntologyNode> nodes = new HashMap<>();
    QueryWrapper wrapper = new QueryWrapper();


    public static void main(String[] args) {
        LeacockCalculator calc = new LeacockCalculator();
        calc.buildOntologyTree();
        String s =  "http://dbpedia.org/ontology/Food";
        String s2 = "http://dbpedia.org/ontology/Flag";
        calc.calculateLeacockChodorow(s, s2);
    }

    public void buildOntologyTree() {
        OntologyNode node = new OntologyNode("owl:Thing");
        buildSubnodes(node);
    }

    private void buildSubnodes(OntologyNode node) {
        List<OntologyNode> children = getSubclasses(node.getResource());
        nodes.put(node.getResource(), node);
        node.setChildren(children);
        for (OntologyNode child : children) {
            child.setParent(node);
            buildSubnodes(child);
        }
    }

    private List<OntologyNode> getSubclasses(String resource) {
        String queryString;
        if (resource.contains("http")) {
            String escapedResource = "<" + resource + ">";
            queryString = "SELECT ?subClass WHERE { ?subClass rdfs:subClassOf " + escapedResource + ". }";
        } else {
            queryString = "SELECT ?subClass WHERE { ?subClass rdfs:subClassOf " + resource + ". }";
        }

        List<OntologyNode> subClasses = new ArrayList<>();
        ResultSet rs = wrapper.executeQueryFragments(queryString);
        //ResultSet rs = wrapper.executeQuery(queryString);

        Set<String> addedSubclasses = new HashSet<>();
        while (rs.hasNext()) {
            QuerySolution solution = rs.next();
            String res = solution.getResource("subClass").toString();
            if (!addedSubclasses.contains(res)) {
                OntologyNode node = new OntologyNode(res);
                addedSubclasses.add(res);
                subClasses.add(node);
            }
        }
        return subClasses;
    }


    public Double calculateLeacockChodorow(String firstResource, String secondResource) {
        OntologyNode first = nodes.get(firstResource);
        OntologyNode second = nodes.get(secondResource);
        Integer length = getDistance(first, second);
        Integer maxDepth = Math.max(getDepth(first), getDepth(second));
        return (- Math.log(length / 2 * maxDepth));
    }

    private int getDepth(OntologyNode node) {
        return getAncestors(node).size();
    }

    private int getDistance(OntologyNode first, OntologyNode second) {
        List<String> firstAncestors = getAncestors(first);
        List<String> secondAncestors = getAncestors(second);
        List<String> intersection = intersection(firstAncestors, secondAncestors);
        int shortestDepth = Math.min(firstAncestors.size(), secondAncestors.size());
        for (int i = 0; i < shortestDepth; i++) {
            if (intersection.contains(firstAncestors.get(i))) {
                return i + secondAncestors.indexOf(firstAncestors.get(i));
            } else if (intersection.contains(secondAncestors.get(i))) {
                return i + firstAncestors.indexOf(secondAncestors.get(i));
            }
        }
        return -1;
    }

    public List<String> intersection(List<String> list1, List<String> list2) {
        List<String> list = new ArrayList<>();

        for (String t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    private List<String> getAncestors(OntologyNode first) {
        List<String> ancestors = new ArrayList<>();
        OntologyNode  current = first;
        while (current.hasParent()) {
            ancestors.add(current.getResource());
            current = current.getParent();
        }
        return ancestors;
    }
}
