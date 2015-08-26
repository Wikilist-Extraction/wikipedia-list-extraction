package TypesCleanUp;

import java.util.List;

public class OntologyNode {
    private OntologyNode parent;
    private String resource;
    private List<OntologyNode> children;

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
        return parent == null;
    }
    public String getResource() {
        return this.resource;
    }
}
