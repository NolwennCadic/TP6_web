import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

/**
 * Created by cadicn on 11/28/18.
 */
public class Bookmarks {

    public static void handleBookmarkList(HttpServletRequest req, HttpServletResponse resp,
                                     Dispatcher.RequestMethod method, String[] requestPath,
                                     Map<String, List<String>> queryParams, User user) throws IOException {
        System.out.println("Action: handleBookmarkList- " + method + "-" + queryParams);
    }

    public static void handleBookmark(HttpServletRequest req, HttpServletResponse resp,
                                      Dispatcher.RequestMethod method, String[] requestPath,
                                      Map<String, List<String>> queryParams, User user) throws IOException {
        System.out.println("Action: handleBookmark - " + method + "-" + queryParams);
        if (method == Dispatcher.RequestMethod.POST){
            resp.setStatus(405);
            return;
        }

        //Handle GET
        if (method == Dispatcher.RequestMethod.GET) {
            /* On récupère l'id que l'utilisateur a entré */
            Long id = (long) Integer.parseInt(requestPath[2]);
            // Test si le bookmark existe
            try {
                if (BookmarkDAO.getBookmarkById(id, user) != null) {
                    System.out.println("existe");
                    String json = "[";
                    json += BookmarkDAO.getBookmarkById(id, user).toJson();
                    json += "]";
                    System.out.println(json);
                    // Send the response
                    resp.setStatus(200);
                    resp.setContentType("application/json");
                    resp.getWriter().print(json);
                    return;
                } else {
                    System.out.println("n'existe pas");
                    resp.setStatus(404);
                    return;
                }
            } catch (SQLException e) {
                System.out.println("catch");
                e.printStackTrace();
            }
        }

        //Handle PUT
        if (method == Dispatcher.RequestMethod.PUT) {
            System.out.println("here put");
            Long id = (long) Integer.parseInt(requestPath[2]);
            try {
                if (BookmarkDAO.getBookmarkById(id, user) != null) {
                    Bookmark bookmark = BookmarkDAO.getBookmarkById(id, user);
                    String newDescription = bookmark.getDescription();
                    String newTitle = bookmark.getTitle();
                    String newLink = bookmark.getLink();
                    List<Tag> newTags = bookmark.getTags();
                    JSONObject jsonBookmark = new JSONObject(queryParams.get("json").get(0));
                    // Test les clefs pour connaitre les champs à modifier
                    if (jsonBookmark.has("description")) {
                        newDescription = jsonBookmark.getString("description");
                    }
                    if (jsonBookmark.has("title")) {
                        newTitle = jsonBookmark.getString("title");
                    }
                    if (jsonBookmark.has("link")) {
                        newLink = jsonBookmark.getString("link");
                    }
                    if (jsonBookmark.has("tags")) {
                        System.out.println("has tags");
                        // on recupere les tags
                        newTags = new ArrayList<>();
                        JSONArray jsonTags = jsonBookmark.getJSONArray("tags");
                        System.out.println("json tags : " + jsonTags);
                        for (int i = 0; i < jsonTags.length(); i++) {
                            System.out.println("i : " + i);
                            JSONObject jsonTag = jsonTags.getJSONObject(i);
                            System.out.println("tag i : " + jsonTag);
                            // Ajoute le nouveau tag à la liste des nouveaux tags
                            newTags.add(new Tag(jsonTag.getString("name")));
                            System.out.println("newTags liste : " + newTags);
                        }
                    }
                    BookmarkDAO.updateBookmark(bookmark, newTitle, newDescription, newLink, newTags, user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
