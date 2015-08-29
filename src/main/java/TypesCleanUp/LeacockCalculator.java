package TypesCleanUp;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import fragmentsWrapper.QueryWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import util.Config;

public class LeacockCalculator {
    private  Map<String, OntologyNode> nodes = new HashMap<>();

    final double leacockThreshold = Config.config().getDouble("leacock.forEachThreshold");
    final double meanThreshold = Config.config().getDouble("leacock.meanThreshold");
    final Boolean useMeanThreshold = Config.config().getBoolean("leacock.useMeanThreshold");

//    private  QueryWrapper wrapper = new QueryWrapper();

    public OntologyNode getNode(String name) {
        if (!nodes.containsKey(name))
            throw new IllegalArgumentException();

        return nodes.get(name);
    }

    public static void main(String[] args) {
        LeacockCalculator calc = new LeacockCalculator();
        //calc.buildOntologyTree();
        calc.buildOntologyTreeFromFile();
        String s =  "http://dbpedia.org/ontology/NaturalPlace";
        String s2 = "http://dbpedia.org/ontology/Place";
        Map<String, Integer> test = new HashMap<>();
        test.put("http://dbpedia.org/ontology/Activity", 13);
        test.put("http://dbpedia.org/ontology/Person", 8);
        test.put("http://dbpedia.org/ontology/HorseRiding", 11);

        System.out.println(calc.calculateLeacockChodorow(s, s2));
    }


    /*
    public void buildOntologyTree() {
        OntologyNode node = new OntologyNode("owl:Thing");
        PrintWriter writer = null;

        buildSubnodes(node);
    }
    */

    public void buildOntologyTreeFromFile() {
        Path path = Paths.get("ontology-seperated.txt");
        try {
            List<String> ontologyRelations = Files.readAllLines(path);
            buildTreeFromLines(ontologyRelations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildTreeFromLines(List<String> statements) {
        Set<String> allResources = new HashSet<>();
        for (String stat : statements) {
            String[] splittedStat = stat.split("\\$");
            String firstNode = splittedStat[0];
            String secondNode = splittedStat[1];

            OntologyNode parent;
            if (!allResources.contains(firstNode)) {
                allResources.add(firstNode);
                parent = new OntologyNode(firstNode);
                nodes.put(parent.getResource(), parent);
            } else {
                parent = nodes.get(firstNode);
            }
            OntologyNode child;

            if (!allResources.contains(secondNode)) {
                allResources.add(secondNode);
                child = new OntologyNode(secondNode);
                nodes.put(child.getResource(), child);
            } else {
                child = nodes.get(secondNode);
            }
            assert (!Objects.equals(parent.getResource(), child.getResource()));
            parent.addChild(child);
            child.setParent(parent);
        }
    }

    public Boolean areTypesSpreaded(Map<String, Integer> typesMap) {
        List<String> types = new ArrayList<>(typesMap.keySet());
        Integer relevanceThreshold = findRelevanceThreshold(typesMap);

        double sum = 0d;
        double count = 0;
        for (int i = 0; i < types.size() - 1; i++) {
            for (int j = i + 1; j < types.size(); j++) {
                String firstType = types.get(i);
                String secondType = types.get(j);
                if (!(firstType.contains("dbpedia") && secondType.contains("dbpedia"))) {
                    continue;
                }
                if (typesMap.get(firstType) < relevanceThreshold || typesMap.get(secondType) < relevanceThreshold) {
                    continue;
                }
                try {
                    double dist = this.calculateLeacockChodorow(firstType, secondType);
                    System.out.println(dist);
                    sum += dist;
                    count++;
                    if (!useMeanThreshold && dist < leacockThreshold) {
                        return true;
                    }
                } catch (Exception e) {
//                    System.out.println("could not calculate Leacock distance from: " + firstType + " and " + secondType);
                }

            }
        }

        if (useMeanThreshold) {
            double avgDist = sum / count;
            System.out.println(sum + " count: " + count);
            return avgDist < meanThreshold;
        }

        return false;
    }

    private Integer findRelevanceThreshold(Map<String, Integer> typesMap) {
        int maximum = Collections.max(typesMap.values());
        return maximum / 5;
    }

    /*
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
    */

    public Double calculateLeacockChodorow(String firstResource, String secondResource) {
        OntologyNode first = nodes.get(firstResource);
        OntologyNode second = nodes.get(secondResource);
        Integer length = getDistance(first, second);
        Integer maxDepth = Math.max(getDepth(first), getDepth(second));
        return (- Math.log((double) length / 2 * maxDepth));
    }

    private int getDepth(OntologyNode node) {
        return getAncestors(node).size();
    }

    private int getDistance(OntologyNode first, OntologyNode second) {
        List<String> firstAncestors = getAncestors(first);
        List<String> secondAncestors = getAncestors(second);
        for (String ancestor : firstAncestors) {
            if (secondAncestors.contains(ancestor)) {
                return firstAncestors.indexOf(ancestor) + secondAncestors.indexOf(ancestor);
            }
        }
        //List<String> intersection = intersection(firstAncestors, secondAncestors);
        /*List<String> intersection = new ArrayList<>();
        int shortestDepth = Math.min(firstAncestors.size(), secondAncestors.size());
        for (int i = 0; i < shortestDepth; i++) {
            if (intersection.contains(firstAncestors.get(i))) {
                return i + secondAncestors.indexOf(firstAncestors.get(i));
            } else if (intersection.contains(secondAncestors.get(i))) {
                return i + firstAncestors.indexOf(secondAncestors.get(i));
            }
            intersection.add();
        }*/

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
        ancestors.add(current.getResource());
        while (current.hasParent()) {
            ancestors.add(current.getParent().getResource());
            current = current.getParent();
        }
        return ancestors;
    }
}
