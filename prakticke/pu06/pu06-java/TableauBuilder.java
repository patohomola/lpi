import java.util.ArrayList;
import java.util.List;

class TableauBuilder {

    public Tableau build(SignedFormula[] sfs) {
        Tableau t = new Tableau();
        List<Node> initialNodes = t.addInitial(sfs);
        List<Node> allNodes = new ArrayList<>();

        for (Node node : initialNodes) {
            allNodes.add(node);
            if (closeLast(allNodes)) {
                return t;
            }
        }

        List<Node> alphaNodes = new ArrayList<>();
        List<Node> betaNodes = new ArrayList<>();

        for (Node node : allNodes) {
            if (node.sf().type() == SignedFormula.Type.Beta) {
                betaNodes.add(node);
            } else {
                alphaNodes.add(node);
            }
        }

        extendBetaNode(alphaNodes, betaNodes, allNodes, t, allNodes.get(allNodes.size()-1));
        return t;
    }

    private boolean closeLast(List<Node> table) {
        int last = table.size()-1;
        for (int i = 0; i < last; i++) {
            if(table.get(i).isClosed())
                return true;
            if (table.get(i).sf().f().equals(table.get(last).sf().f()) &&
                    table.get(i).sf().sign() != table.get(last).sf().sign()) {
                table.get(last).close(table.get(i));
                return true;
            }
        }
        return false;
    }

    private void extendBetaNode(List<Node> alphaNodes, List<Node> betaNodes, List<Node> nodes, Tableau tableau, Node leaf) {

        Node out = extendAlphas(alphaNodes, betaNodes, nodes, tableau, leaf);
        if (out == null)
            return;
        leaf = out;

        if (!closeLast(nodes) && !betaNodes.isEmpty()) {
            Node parent = betaNodes.get(0);
            betaNodes.remove(0);
            List<Node> output = tableau.extendBeta(leaf, parent);
            for (Node newBranch : output) {
                var alphaNodes2 = new ArrayList<>(alphaNodes);
                var betaNodes2 = new ArrayList<>(betaNodes);
                var nodes2 = new ArrayList<>(nodes);
                nodes2.add(newBranch);
                boolean b = (newBranch.sf().type() == SignedFormula.Type.Beta) ? betaNodes2.add(newBranch) : alphaNodes2.add(newBranch);
                extendBetaNode(alphaNodes2, betaNodes2, nodes2, tableau, newBranch);
            }
        }
    }

    private Node extendAlphas(List<Node> alphaNodes, List<Node> betaNodes, List<Node> nodes, Tableau t, Node leaf) {
        while (!closeLast(nodes) && !alphaNodes.isEmpty()){
            Node parent = alphaNodes.get(0);
            alphaNodes.remove(0);
            for (int i = 0; i < parent.sf().subf().size(); i++) {
                leaf = t.extendAlpha(leaf, parent, i);
                nodes.add(leaf);
                if (leaf.sf().type() == SignedFormula.Type.Beta){
                    betaNodes.add(leaf);
                } else alphaNodes.add(leaf);
                if (closeLast(nodes))
                    return null;
            }
        }
        return leaf;
    }
}
