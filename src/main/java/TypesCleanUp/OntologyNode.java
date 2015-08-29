package TypesCleanUp;

import java.util.ArrayList;
import java.util.List;

public class OntologyNode {
    private OntologyNode parent;
    private String resource;
    private List<OntologyNode> children = new ArrayList<>();

    public OntologyNode getParent() {
        return parent;
    }
    public OntologyNode(String resource) {
        this.resource = resource;
    }
    public void setChildren(List<OntologyNode> children) {
        this.children = children;
    }
    public void setParent(OntologyNode parent) {
        this.parent = parent;
    }
    public boolean hasParent() {
        return parent != null;
    }
    public String getResource() {
        return this.resource;
    }
    public void addChild(OntologyNode child) {
        this.children.add(child);
    }
}
