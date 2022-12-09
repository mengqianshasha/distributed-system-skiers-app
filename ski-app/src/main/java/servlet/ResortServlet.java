package servlet;

import redis.RedisClient;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/resorts/*")
public class ResortServlet extends HttpServlet {
    private static final String numOfSkiersPatternStr = "^\\/(\\d+)\\/seasons\\/(\\d+)\\/day\\/(\\d+)\\/skiers$";
    public RedisClient redisClient;

    @Override
    public void init() {
        this.redisClient = new RedisClient();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();
        Pattern numOfSkiersPattern = Pattern.compile(numOfSkiersPatternStr, Pattern.CASE_INSENSITIVE);
        Matcher numOfSkiersMatcher = numOfSkiersPattern.matcher(urlPath);

        long numOfSkiers = 0;
        boolean isValid = false;
        if (numOfSkiersMatcher.find()) {
            isValid = true;
            numOfSkiers = this.redisClient.getNumOfSkiers(numOfSkiersMatcher.group(1), numOfSkiersMatcher.group(2), numOfSkiersMatcher.group(3));
        }

        if (isValid && numOfSkiers != 0) {
            res.setStatus(200);
            res.getWriter().write(Long.toString(numOfSkiers));
        } else if (!isValid) {
            res.setStatus(400);
            res.getWriter().write("Invalid Input");
        } else {
            res.setStatus(404);
            res.getWriter().write("Data Not Found");
        }
    }
}
