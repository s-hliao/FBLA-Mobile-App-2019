package BaseClasses;

import java.util.HashSet;
import java.util.TreeSet;

public class Chapter {
    private int name;
    private HashSet<Account> accounts;
    private HashSet<Integer> advisorIndex;

    public Chapter(int name){
        this.name = name;
        accounts =  new HashSet<Account>();
        advisorIndex = new HashSet<Integer>();
    }

    public void addAdvisor(Account adv, String pos){
        advisorIndex.add(adv.hashCode());
    }



}
