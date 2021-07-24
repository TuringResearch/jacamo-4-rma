// Internal action code for project farmerSMA

package jia;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class isRaining extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'jia.chuvas'");
        
        try {
            Document doc = Jsoup.connect(
                    "https://www.google.com/search?sxsrf=ACYBGNSLmJesKN6cWq3L4_Zu5lfQdbt-Eg%3A1581894106786&ei=2slJXuLbL_G_5OUP6MyboAo&q=previs%C3%A3o+do+tempo+rio+de+janeiro+tempo+real&oq=prev&gs_l=psy-ab.3.0.35i39i285i70i256j0i67l2j0i131i67j0i67l2j0l4.10260.11346..12622...1.2..1.763.1312.0j2j1j6-1......0....1..gws-wiz.....10..0i71j35i362i39j35i39i285j35i39.IdguNccBtgc")
                    .get();
            String ans = doc.getElementById("wob_pp").getElementsByTag("span").text();
            String weather = ans.replace("%", "");
            Integer num = Integer.parseInt(weather);
            if (num > 80){
                System.out.println("It's going to rain.");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // everything ok, so returns true
        return false;
    }
}
