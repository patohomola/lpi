import java.util.*;

class Constant {
    String name;
    public Constant(String name) {
        this.name = name;
    }
    public String name() {
        return this.name;
    }
    public String eval(Structure m) {
        return m.iC(name());
    }
    @Override
    public String toString() {
        return name();
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Constant otherC = (Constant) other;
        return name().equals(otherC.name());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}

class Formula {
    public List<Formula> subfs() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Not implemented");
    }

    public boolean isTrue(Structure m) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object other) {
        throw new RuntimeException("Not implemented");
    }

    public int deg() {
        throw new RuntimeException("Not implemented");
    }

    public Set<AtomicFormula> atoms() {
        throw new RuntimeException("Not implemented");
    }

    public Set<String> constants() {
        throw new RuntimeException("Not implemented");
    }

    public Set<String> predicates() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Return the type (of a signed formula),
     * as if this was a signed formula signed with sign.
     *
     * @param sign the sign of the signed formula to be considered
     * @return SignedFormula.Type.Alpha or SignedFormula.Type.Beta
     */
    public SignedFormula.Type signedType(boolean sign) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Return a list of signed sub-formulas of this
     * formula, if this formula was signed with sign
     *
     * @param sign the sign of the signed formula to be considered
     * @return list of signed sub-formulas
     */
    public List<SignedFormula> signedSubf(boolean sign) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}

class AtomicFormula extends Formula {
    AtomicFormula() {}
    @Override
    public List<Formula> subfs() {
        return List.of();
    }

    @Override
    public int deg() {
        return 0;
    }

    @Override
    public Set<AtomicFormula> atoms() {
        return Set.of(this);
    }
    @Override
    public SignedFormula.Type signedType(boolean sign) {
        return SignedFormula.Type.Alpha;
    }

    @Override
    public List<SignedFormula> signedSubf(boolean sign) {
        List<SignedFormula> formulas = new ArrayList<>();
        return formulas;
    }
}

class PredicateAtom extends AtomicFormula {
    private final String name;
    private final List<Constant> args;

    PredicateAtom(String name, List<Constant> args) {
        this.name = name;
        this.args = args;
    }

    String name() {
        return this.name;
    }

    List<Constant> arguments() {
        return this.args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Constant arg : args) {
            if (!sb.isEmpty()) {
                sb.append(",");
            }

            sb.append(arg.toString());
        }

        return name + "(" + sb + ")";
    }

    @Override
    public boolean isTrue(Structure m) {
        List<String> argVals = new ArrayList<>();
        for (Constant arg: arguments()) {
            argVals.add(arg.eval(m));
        }
        return m.iP(name()).contains(argVals);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;

        PredicateAtom otherA = (PredicateAtom) other;
        if (!name.equals(otherA.name())) return false;

        List<Constant> otherArgs = otherA.arguments();
        if (args.size() != otherArgs.size()) return false;

        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).equals(otherArgs.get(i))) return false;
        }

        return true;
    }

    @Override
    public Set<String> constants() {
        Set<String> cons = new HashSet<>();

        for (Constant con : args) {
            cons.add(con.name());
        }

        return cons;
    }

    @Override
    public Set<String> predicates() {
        return new HashSet<>(Collections.singletonList(name));
    }
}

class Negation extends Formula {
    private final Formula origForm;

    Negation(Formula originalFormula) {
        this.origForm = originalFormula;
    }

    public Formula originalFormula() {
        return this.origForm;
    }

    @Override
    public List<Formula> subfs() {
        return Collections.singletonList(origForm);
    }

    @Override
    public String toString() {
        return "-" + origForm.toString();
    }

    @Override
    public boolean isTrue(Structure m) {
        return !origForm.isTrue(m);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;

        Negation otherN = (Negation) other;

        return origForm.equals(otherN.originalFormula());
    }

    @Override
    public int deg() {
        return origForm.deg() + 1;
    }

    @Override
    public Set<AtomicFormula> atoms() {
        return origForm.atoms();
    }

    @Override
    public Set<String> constants() {
        return origForm.constants();
    }

    @Override
    public Set<String> predicates() {
        return origForm.predicates();
    }
    @Override
    public SignedFormula.Type signedType(boolean sign) {
        return SignedFormula.Type.Alpha;
    }
    @Override
    public List<SignedFormula> signedSubf(boolean sign)
    {
        List<SignedFormula> e = new ArrayList<>();
        e.add(new SignedFormula(!sign, this.origForm));
        return e;
    }
}

class Disjunction extends Formula {
    private final List<Formula> disjuncts;

    Disjunction(List<Formula> disjuncts) {
        this.disjuncts = disjuncts;
    }

    @Override
    public List<Formula> subfs() {
        return disjuncts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Formula form : disjuncts) {
            if (!sb.isEmpty()) {
                sb.append("|");
            }

            sb.append(form.toString());
        }

        return "(" + sb + ")";
    }

    @Override
    public boolean isTrue(Structure m) {
        for (Formula form : disjuncts) {
            if (form.isTrue(m)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;

        Disjunction otherD = (Disjunction) other;

        List<Formula> otherDisjuncts = otherD.subfs();
        if (disjuncts.size() != otherDisjuncts.size()) return false;

        for (int i = 0; i < disjuncts.size(); i++) {
            if (!disjuncts.get(i).equals(otherDisjuncts.get(i))) return false;
        }

        return true;
    }

    @Override
    public int deg() {
        int deg = disjuncts.size() - 1;

        for (Formula form : disjuncts) {
            deg += form.deg();
        }

        return Math.max(deg, 1);
    }

    @Override
    public Set<AtomicFormula> atoms() {
        Set<AtomicFormula> atoms = new HashSet<>();

        for (Formula form : disjuncts) {
            atoms.addAll(form.atoms());
        }

        return atoms;
    }

    @Override
    public Set<String> constants() {
        Set<String> cons = new HashSet<>();

        for (Formula form : disjuncts) {
            cons.addAll(form.constants());
        }

        return cons;
    }

    @Override
    public Set<String> predicates() {
        Set<String> preds = new HashSet<>();

        for (Formula form : disjuncts) {
            preds.addAll(form.predicates());
        }

        return preds;
    }
    @Override
    public SignedFormula.Type signedType(boolean sign) {
        if(sign)
            return SignedFormula.Type.Beta;
        return SignedFormula.Type.Alpha;
    }
    @Override
    public List<SignedFormula> signedSubf(boolean sign)
    {
        List<SignedFormula> signedFormulas = new ArrayList<>();
        for (Formula f:disjuncts) {
            signedFormulas.add(new SignedFormula(sign,f));
        }
        return signedFormulas;
    }
}

class Conjunction extends Formula {
    private final List<Formula> conjuncts;

    Conjunction(List<Formula> conjuncts) {
        this.conjuncts = conjuncts;
    }

    @Override
    public List<Formula> subfs() {
        return conjuncts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Formula form : conjuncts) {
            if (!sb.isEmpty()) {
                sb.append("&");
            }

            sb.append(form.toString());
        }

        return "(" + sb + ")";
    }

    @Override
    public boolean isTrue(Structure m) {
        for (Formula form : conjuncts) {
            if (!form.isTrue(m)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;

        Conjunction otherD = (Conjunction) other;

        List<Formula> otherConjucts = otherD.subfs();
        if (conjuncts.size() != otherConjucts.size()) return false;

        for (int i = 0; i < conjuncts.size(); i++) {
            if (!conjuncts.get(i).equals(otherConjucts.get(i))) return false;
        }

        return true;
    }

    @Override
    public int deg() {
        int deg = conjuncts.size() - 1;

        for (Formula form : conjuncts) {
            deg += form.deg();
        }

        return Math.max(deg, 1);
    }

    @Override
    public Set<AtomicFormula> atoms() {
        Set<AtomicFormula> atoms = new HashSet<>();

        for (Formula form : conjuncts) {
            atoms.addAll(form.atoms());
        }

        return atoms;
    }

    @Override
    public Set<String> constants() {
        Set<String> cons = new HashSet<>();

        for (Formula form : conjuncts) {
            cons.addAll(form.constants());
        }

        return cons;
    }

    @Override
    public Set<String> predicates() {
        Set<String> preds = new HashSet<>();

        for (Formula form : conjuncts) {
            preds.addAll(form.predicates());
        }

        return preds;
    }

    @Override
    public SignedFormula.Type signedType(boolean sign) {
        if(sign)
            return SignedFormula.Type.Alpha;
        return SignedFormula.Type.Beta;
    }
    @Override
    public List<SignedFormula> signedSubf(boolean sign)
    {
        List<SignedFormula> signedFormulas = new ArrayList<>();
        for (Formula f:conjuncts) {
            signedFormulas.add(new SignedFormula(sign,f));
        }
        return signedFormulas;
    }
}

class BinaryFormula extends Formula {
    final Formula left;
    final Formula right;

    BinaryFormula(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }

    public Formula leftSide() {
        return this.left;
    }

    public Formula rightSide() {
        return this.right;
    }

    @Override
    public List<Formula> subfs() {
        return Arrays.asList(left, right);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;

        BinaryFormula otherB = (BinaryFormula) other;

        return left.equals(otherB.leftSide()) && right.equals(otherB.rightSide());
    }

    @Override
    public int deg() {
        return left.deg() + right.deg() + 1;
    }

    @Override
    public Set<AtomicFormula> atoms() {
        Set<AtomicFormula> atoms = new HashSet<>(left.atoms());
        atoms.addAll(right.atoms());

        return atoms;
    }

    @Override
    public Set<String> constants() {
        Set<String> cons = new HashSet<>(left.constants());
        cons.addAll(right.constants());

        return cons;
    }

    @Override
    public Set<String> predicates() {
        Set<String> preds = new HashSet<>(left.predicates());
        preds.addAll(right.predicates());

        return preds;
    }
}

class Implication extends BinaryFormula {
    Implication(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "->" + right.toString() + ")";
    }

    @Override
    public boolean isTrue(Structure m) {
        return !left.isTrue(m) || right.isTrue(m);
    }
    @Override
    public SignedFormula.Type signedType(boolean sign) {
        if(sign)
            return SignedFormula.Type.Beta;
        return SignedFormula.Type.Alpha;
    }
    @Override
    public List<SignedFormula> signedSubf(boolean sign)
    {
        List<SignedFormula> signedFormulas = new ArrayList<>();
        signedFormulas.add(new SignedFormula(!sign,left));
        signedFormulas.add(new SignedFormula(sign,right));
        return signedFormulas;
    }
}

class Equivalence extends BinaryFormula {
    Equivalence(Formula left, Formula right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + "<->" + right.toString() + ")";
    }

    @Override
    public boolean isTrue(Structure m) {
        return (left.isTrue(m) && right.isTrue(m)) || (!left.isTrue(m) && !right.isTrue(m));
    }
    @Override
    public SignedFormula.Type signedType(boolean sign) {
        if(sign)
            return SignedFormula.Type.Alpha;
        return SignedFormula.Type.Beta;
    }
    @Override
    public List<SignedFormula> signedSubf(boolean sign)
    {
        List<SignedFormula> signedFormulas = new ArrayList<>();
        signedFormulas.add(new SignedFormula(sign,new Implication(left,right)));
        signedFormulas.add(new SignedFormula(sign,new Implication(right,left)));
        return signedFormulas;
    }
}
