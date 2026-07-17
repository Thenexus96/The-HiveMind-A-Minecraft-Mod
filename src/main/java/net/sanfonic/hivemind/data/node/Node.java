package net.sanfonic.hivemind.data.node;

/** Simple claimable node definition */
public class Node {
    private final String id;
    private final String resourceType;
    private final int resourceAmount;

    public Node(String id, String resourceType, int resourceAmount) {
        this.id = id;
        this.resourceType = resourceType;
        this.resourceAmount = resourceAmount;
    }

    public String getId() { return id; }
    public String getResourceType() { return resourceType; }
    public int getResourceAmount() { return resourceAmount; }
}
