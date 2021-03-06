package evoker;

import java.io.*;
import java.util.ArrayList;

public abstract class BinaryData {

    protected byte[] bedMagic;
    protected byte[] bntMagic;

    protected MarkerData md;
    protected String collection;
    protected String chromosome;
    protected int numInds;
    protected int numSNPs;
    protected int bytesPerRecord;

    /** default header offset for bnt files */
    protected int bntHeaderOffset;
    /** default header offset for bed files */
    protected int bedHeaderOffset;
     /** to hold the total number of snps when using remote data, as the total number of snps is required for checking Oxformat header information*/
    protected int totNumSNPs;

    BinaryData(int numInds, MarkerData md, String collection, String chromosome){
        this.numInds = numInds;
        this.numSNPs = md.getNumSNPs(collection + chromosome);
        this.totNumSNPs = md.getNumSNPs(collection);
        this.md = md;
        this.collection = collection;
        this.chromosome = chromosome;

        bedMagic = new byte[]{0x6c, 0x1b, 0x01};
        bntMagic = new byte[]{0x1a, 0x31};
        bntHeaderOffset = 2;
        bedHeaderOffset = 3;
    }

    public abstract ArrayList getRecord(String markerName) throws IOException;

    public abstract void checkFile(byte[] headers) throws IOException;

}
