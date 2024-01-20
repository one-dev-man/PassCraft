package mc.passcraft.apis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mc.passcraft.types.Pair;
import mc.passcraft.types.cache.Cache;
import mc.passcraft.types.cache.CacheDataPersistency;
import mc.passcraft.types.cache.CachePersistency;
import mc.passcraft.utils.REST;

import com.google.gson.JsonObject;

public class MojangAPI {

    public enum ENDPOINTS {
        ROOT("https://api.mojang.com"),

        USERNAME_TO_UUID(ROOT, "%%root%%/users/profiles/minecraft/%%username%%");

        //

        private String endpoint;

        //

        ENDPOINTS(String endpoint) {
            this.endpoint = endpoint;
        }

        ENDPOINTS(ENDPOINTS root, String endpoint) {
            this(root.parse(Map.of()), endpoint);
        }

        ENDPOINTS(String root, String endpoint) {
            this.endpoint = endpoint;
            this.endpoint = this.parse(Map.of("root", root));
        }

        //

        public String parse(Map<String, String> args) {
            String result = ""+endpoint;

            List<String> keylist = new ArrayList<>(args.keySet());
            int keylist_length = keylist.size();

            String key;
            for(int i = 0; i < keylist_length; ++i) {
                key = keylist.get(i);
                result = result.replaceAll("%%"+key+"%%", args.get(key));
            }

            return result;
        }
    }

    //

    public enum CACHE_KEY {
        UUID();

        //

        private CACHE_KEY() {
        }

        //

        public String key(Object data) {
            return this.name()+":"+data.toString();
        }
    }

    public static Cache<String, Pair<REST.Response, Object>> CACHE = new Cache<>(CachePersistency.PERSISTENT);

    //

    public static class user {

        public static UUID uuid(String username) throws IOException {
            UUID result;

            REST.Response response;
            if(CACHE.expired(CACHE_KEY.UUID.key(username))) {
                response = REST.request(
                    ENDPOINTS.USERNAME_TO_UUID.parse(Map.of("username", username)),
                    REST.METHOD.GET,
                    Map.of()
                );

                if(response.code() != HttpURLConnection.HTTP_OK)
                    result = null;
                else {
                    JsonObject jsonresp = REST.GSON.fromJson(response.content(), JsonObject.class);
                    String trimmed_uuid = jsonresp.get("id").getAsString();

                    result = UUID.fromString(
                            trimmed_uuid.length() == 32
                                    ? trimmed_uuid.substring(0, 8)
                                    + '-' + trimmed_uuid.substring(8, 12)
                                    + '-' + trimmed_uuid.substring(12, 16)
                                    + '-' + trimmed_uuid.substring(16, 20)
                                    + '-' + trimmed_uuid.substring(20)
                                    : trimmed_uuid
                    );
                }

                CACHE.store(CACHE_KEY.UUID.key(username), Pair.of(response, result), CacheDataPersistency.VOLATILE, Cache.DELAY.MINUTES(5));
            }
            else
                result = (UUID) CACHE.get(CACHE_KEY.UUID.key(username)).last();

            return result;
        }

        public static boolean isPremium(String username) throws IOException {
            return uuid(username) != null;
        }

    }

    //

    

}
