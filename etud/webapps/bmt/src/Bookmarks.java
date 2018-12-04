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

    /**
     * Gere /bookmarks
     * @param req
     * @param resp
     * @param method
     * @param requestPath
     * @param queryParams
     * @param user
     * @throws IOException
     */
    public static void handleBookmarkList(HttpServletRequest req, HttpServletResponse resp,
                                     Dispatcher.RequestMethod method, String[] requestPath,
                                     Map<String, List<String>> queryParams, User user) throws IOException {
        System.out.println("Action: handleBookmarkList- " + method + "-" + queryParams);
        // Handle the get
        if (method == Dispatcher.RequestMethod.GET) {
            /* On récupère l'id que l'utilisateur a entré */
            // Test si le bookmark existe
            try {
                List<Bookmark> bookmarks = BookmarkDAO.getBookmarks(user);
                String json = "[";
                for (int i = 0, n = bookmarks.size(); i < n; i++) {
                    Bookmark bookmark = bookmarks.get(i);
                    json += bookmark.toJson();
                    if (i < n - 1)
                        json += ", ";
                }
                json += "]";
                // Send the response
                resp.setStatus(200);
                resp.setContentType("application/json");
                resp.getWriter().print(json);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Handle the post
        if (method == Dispatcher.RequestMethod.POST) {
            String newDescription = "";
            String newTitle;
            String newLink;
            List<Tag> newTags = new ArrayList<>();
            JSONObject jsonBookmark = new JSONObject(queryParams.get("json").get(0));
            // Parse le json
            if (jsonBookmark.has("description")) {
                newDescription = jsonBookmark.getString("description");
            }
            if (jsonBookmark.has("title")) {
                newTitle = jsonBookmark.getString("title");
            } else {
                resp.setStatus(405);
                return;
            }
            if (jsonBookmark.has("link")) {
                newLink = jsonBookmark.getString("link");
            } else {
                resp.setStatus(405);
                return;
            }
            if (jsonBookmark.has("tags")) {
                // on recupere les tags
                newTags = new ArrayList<>();
                JSONArray jsonTags = jsonBookmark.getJSONArray("tags");
                for (int i = 0; i < jsonTags.length(); i++) {
                    JSONObject jsonTag = jsonTags.getJSONObject(i);
                    //cherche si le tag existe
                    Tag tag = new Tag(jsonTag.getString("name"));
                    // Si le tag existe déjà
                    try {
                        if (TagDAO.getTagByName(tag.getName(), user) != null) {
                            newTags.add(TagDAO.getTagByName(tag.getName(), user));
                        }
                        else {
                            newTags.add(tag);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                if (BookmarkDAO.getBookmarkByLink(newLink, user) != null) {
                    // Rien n'est modifie car le bookmark existe déjà
                    resp.setStatus(304);
                    return;
                } else {
                    // Ajoute le nouveau bookmark dans la BD
                    BookmarkDAO.saveBookmark(newDescription, newLink, newTitle, user);
                    // Recupere son ID :
                    Long bookmarkId = BookmarkDAO.getBookmarkByLink(newLink, user).getId();
                    // Attache les tags
                    for (Tag tag : newTags) {
                        // Cas où le tag existe dans la BD
                        if (tag.getId() != null) {
                            TagDAO.attachedBookMark(bookmarkId, tag.getId());
                        }
                        // Cas où il n'existe pas, on le crée puis on l'attache au bookmark
                        else {
                            // On crée le tag :
                            TagDAO.saveTag(tag, user);
                            // on récupère le nouvel ID
                            Tag newTag = TagDAO.getTagByName(tag.getName(), user);
                            // on attache le tag
                            TagDAO.attachedBookMark(bookmarkId, newTag.getId());
                        }
                    }
                    resp.setStatus(201);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            resp.setStatus(405);
            return;
        }
    }

    /**
     * Gère /bookmarks/<bid>
     * @param req
     * @param resp
     * @param method
     * @param requestPath
     * @param queryParams
     * @param user
     * @throws IOException
     */
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
                    String json = "[";
                    json += BookmarkDAO.getBookmarkById(id, user).toJson();
                    json += "]";
                    // Send the response
                    resp.setStatus(200);
                    resp.setContentType("application/json");
                    resp.getWriter().print(json);
                    return;
                } else {
                    resp.setStatus(404);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Handle PUT
        if (method == Dispatcher.RequestMethod.PUT) {
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
                        System.out.println("bm id : " + id);
//                        System.out.println("new bm id " + BookmarkDAO.getBookmarkByLink(newLink, user).getId());
//                        boolean b = BookmarkDAO.getBookmarkByLink(newLink, user).getId() != id;
//                        System.out.println("condition : " + b);
                        // Test si le nouveau lien est valide, donc si il n'est pas déjà utilisé par un autre atg
                        if (BookmarkDAO.getBookmarkByLink(newLink, user) != null &&
                                BookmarkDAO.getBookmarkByLink(newLink, user).getId() != id) {
                            System.out.println("dans if");
                            resp.setStatus(403);
                            return;
                        }
                    }
                    if (jsonBookmark.has("tags")) {
                        // on recupere les tags
                        newTags = new ArrayList<>();
                        JSONArray jsonTags = jsonBookmark.getJSONArray("tags");
                        for (int i = 0; i < jsonTags.length(); i++) {
                            JSONObject jsonTag = jsonTags.getJSONObject(i);
                            // Ajoute le nouveau tag à la liste des nouveaux tags
                            newTags.add(new Tag(jsonTag.getString("name")));
                        }
                    }
                    BookmarkDAO.updateBookmark(bookmark, newTitle, newDescription, newLink, newTags, user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Handle the delete
        if (method == Dispatcher.RequestMethod.DELETE) {
            // On récupère l'id du bookmark
            Long id = (long) Integer.parseInt(requestPath[2]);
            // On recupere le bookmark s'il existe
            try {
                if (BookmarkDAO.getBookmarkById(id, user) != null) {
                    Bookmark bookmark = BookmarkDAO.getBookmarkById(id, user);
                    // On supprime l'attachement des tags au bookmark
                    for (Tag tag: bookmark.getTags()) {
                        TagDAO.removeAttachmentBookmark(id, tag.getId());
                    }
                    // On supprime le bookmark
                    BookmarkDAO.deleteBookmark(id, user);
                    resp.setStatus(204);
                    return;
                } else {
                    resp.setStatus(403);
                    return;
                }

            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
