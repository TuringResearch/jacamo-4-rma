// Internal action code for project farmerSMA

package action;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class isRaining extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        try {
        	Document doc = Jsoup.connect(
                    "https://www.google.com/search?q=previs%C3%A3o+do+tempo")
                    .get();
            String ans = doc.getElementById("wob_pp").getElementsByTag("span").text();
            String weather = ans.replace("%", "");
            Integer num = Integer.parseInt(weather);
            if (num > 80){
                System.out.println("It's going to rain.");
                return true;
            }
        } catch (IOException e) {
        	ts.getAg().getLogger().info("[INFO] error in isRining.");
        }
        // everything ok, so returns true
        return false;
    }
}
