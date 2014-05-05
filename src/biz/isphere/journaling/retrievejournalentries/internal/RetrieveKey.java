package biz.isphere.journaling.retrievejournalentries.internal;

/**
 * Enumeration, representing the keys of the QjoRetrieveJournalEntries API for
 * selecting journal entries.
 * 
 * @author Stanley, Thomas Raddatz
 */
public enum RetrieveKey {
    RCVRNG /*     */(1, "Range of journal receivers"),
    FROMENT/*     */(2, "Starting sequence number"),
    FROMTIME/*    */(3, "Starting time stamp"),
    TOENT /*      */(4, "Ending sequence number"),
    TOTIME /*     */(5, "Ending time stamp"),
    NBRENT /*     */(6, "Number of entries"),
    JRNCDE /*     */(7, "Journal codes"),
    ENTTYP /*     */(8, "Journal entry types"),
    JOB /*        */(9, "Job"),
    PGM /*        */(10, "Program"),
    USRPRF /*     */(11, "User profile"),
    CMTCYCID /*   */(12, "Commit cycle identifier"),
    DEPENT /*     */(13, "Dependent entries"),
    INCENT /*     */(14, "Include entries"),
    NULLINDLEN /* */(15, "Null value indicators length"),
    FILE /*       */(16, "File"),
    OBJ /*        */(17, "Object"),
    OBJPATH /*    */(18, "Object Path"),
    OBJFID /*     */(19, "Object file identifier"),
    SUBTREE /*    */(20, "Directory substree"),
    PATTERN /*    */(21, "Name pattern"),
    FMTMINDTA /*  */(22, "Format Minimized Data");

    private int key;
    private String description;

    private RetrieveKey(int aKey, String aDescription) {
        key = aKey;
        description = aDescription;
    }

    public int getKey() {
        return this.key;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%s, (%d)", getDescription(), getKey());
    }
};
