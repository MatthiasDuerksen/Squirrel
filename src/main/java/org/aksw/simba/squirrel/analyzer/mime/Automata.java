package org.aksw.simba.squirrel.analyzer.mime;

import org.apache.jena.riot.Lang;

public class Automata implements FiniteStateMachine {

    /**
     * Current state.
     */
    private State current;
    private Lang mimeType;

    /**
     * Ctor.
     *
     * @param initial Initial state of this machine.
     */
    public Automata(final State initial, final Lang type) {
        this.current = initial;
        this.mimeType = type;
    }

    public void switchState(final String c) {
        this.current = this.current.transit(c);
    }


    public boolean isError() {
        return this.current.isError();
    }

    public Lang getMimeType() {
        return this.mimeType;
    }
}
