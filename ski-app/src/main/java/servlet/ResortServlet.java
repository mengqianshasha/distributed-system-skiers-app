package servlet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import redis.RedisClient;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/resorts/*")
public class ResortServlet extends HttpServlet {
    private static final String numOfSkiersPatternStr = "^\\/(\\d+)\\/seasons\\/(\\d+)\\/day\\/(\\d+)\\/skiers$";
    private RedisClient redisClient;
    private Cache<String, Response> cache;

    @Override
    public void init() {
        this.redisClient = new RedisClient();
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");

        /////////  Option1: cache   ///////////
//        Response response = this.cache.get(req.getPathInfo(), path -> this.getResponse(req));
//        res.setStatus(response.getStatusCode());
//        res.getWriter().write(response.getMessage());

        /////////  Option2: no cache   ///////////
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

    private Response getResponse(HttpServletRequest req) {
        String urlPath = req.getPathInfo();
        Pattern numOfSkiersPattern = Pattern.compile(numOfSkiersPatternStr, Pattern.CASE_INSENSITIVE);
        Matcher numOfSkiersMatcher = numOfSkiersPattern.matcher(urlPath);

        long numOfSkiers = 0;
        boolean isValid = false;
        if (numOfSkiersMatcher.find()) {
            isValid = true;
            numOfSkiers = this.redisClient.getNumOfSkiers(numOfSkiersMatcher.group(1), numOfSkiersMatcher.group(2), numOfSkiersMatcher.group(3));
        }

        Response response = new Response();
        if (isValid && numOfSkiers != 0) {
            response.setStatusCode(200);
            response.setMessage(Long.toString(numOfSkiers));
        } else if (!isValid) {
            response.setStatusCode(400);
            response.setMessage("Invalid Input");
        } else {
            response.setStatusCode(404);
            response.setMessage("Data Not Found");
        }

        return response;
    }
}
