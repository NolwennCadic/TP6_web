import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cadicn on 11/28/18.
 */
public class Bookmark {
    /**
     * BookMark ID
     */
    private Long id = null;

    /**
     * Bookmark description
     */
    private String description;

    /**
     * Bookmark title
     */
    private String title;

    /**
     * BookMark link
     */
    private String link;

    /**
     * List of the related tags
     */
    private List<Tag> tags;

    public Bookmark(Long id, String description, String title, String link) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.link= link;
        this.tags = new ArrayList<Tag>();
    }

    public Bookmark(String description, String title, String Link) {
        this.description = description;
        this.title = title;
        this.link = link;
        this.tags = new ArrayList<Tag>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Encodes the bookmark in JSON.
     *
     * @return JSON representation of the tag
     */
    public String toJson() {
        String json = "{";
        // Boolean to handle the ","
        boolean prec = false;
        if (id != null) {
            json += "\"id\":" + id;
            prec = true;
        }
        if(description != null) {
            if (prec) {
                json += ", ";
                prec = true;
            }
            json += "\"description\":\"" + description + "\"";
        } else {
            prec = false;
        }
        if (title != null) {
            if (prec) {
                json += ", ";
                prec = true;
            }
            json += "\"title\":\"" + title + "\"";
        } else {
            prec = false;
        }
        if (link != null) {
            if (prec) {
                json += ", ";
                prec = true;
            }
            json += "\"link\":\"" + link + "\"";
        } else {
            prec = false;
        }
        if (prec) {
            json += ", ";
        }
        json += "\"tags\": [";
        for (int i = 0, n = tags.size(); i < n; i++) {
            Tag tag = tags.get(i);
            json += tag.toJson();
            if (i < n - 1)
                json += ", ";
        }
        json += "]}";
        return json;
    }

    /**
     * Test if a bookmark contains a given tag
     * @param id the id of the tested tag
     * @return true or false
     */
    public boolean containsTag(Long id) {
        for(Tag tag : this.tags) {
            if (tag.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove the given tag of the list tags
     * @param id the ID of the tag to remove
     */
    public void removeTag(Long id) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : this.tags) {
            if (tag.getId() != id) {
                tags.add(tag);
            }
        }
        this.tags = tags;
    }
}
