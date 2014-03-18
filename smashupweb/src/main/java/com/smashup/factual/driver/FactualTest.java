package com.smashup.factual.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.factual.driver.Boost;
import com.factual.driver.BoostResponse;
import com.factual.driver.Circle;
import com.factual.driver.Clear;
import com.factual.driver.ClearResponse;
import com.factual.driver.ColumnSchema;
import com.factual.driver.DiffsCallback;
import com.factual.driver.DiffsQuery;
import com.factual.driver.DiffsResponse;
import com.factual.driver.FacetQuery;
import com.factual.driver.FacetResponse;
import com.factual.driver.Factual;
import com.factual.driver.FactualApiException;
import com.factual.driver.FactualStream;
import com.factual.driver.FlagResponse;
import com.factual.driver.Geocode;
import com.factual.driver.Geopulse;
import com.factual.driver.Insert;
import com.factual.driver.InsertResponse;
import com.factual.driver.JsonUtil;
import com.factual.driver.MatchQuery;
import com.factual.driver.Metadata;
import com.factual.driver.MultiRequest;
import com.factual.driver.MultiResponse;
import com.factual.driver.Point;
import com.factual.driver.Query;
import com.factual.driver.RawReadResponse;
import com.factual.driver.ReadResponse;
import com.factual.driver.Rectangle;
import com.factual.driver.ResolveQuery;
import com.factual.driver.ResolveResponse;
import com.factual.driver.Response;
import com.factual.driver.RowQuery;
import com.factual.driver.RowResponse;
import com.factual.driver.SchemaResponse;
import com.factual.driver.Submit;
import com.factual.driver.SubmitResponse;
import com.factual.driver.UrlUtil;
import com.google.api.client.http.HttpHeaders;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Integration tests for the Factual Java driver. Expects your key and secret to
 * be configured in ~/.factual/factual-auth.yaml
 * 
 * factual-auth.yaml should look like:
 * 
 * <pre>
 * ---
 * key: MY_KEY
 * secret: MY_SECRET
 * </pre>
 * 
 * @author aaron
 */
public class FactualTest {
  private static String TABLE = "places";
  private static String FULL_TABLE = "t/" + TABLE;
  private static String PLACES_V3 = "places-v3";
  private static String SANDBOX_TABLE = "us-sandbox";
  private static Factual factual;
  //TODO private static final File AUTH = new File(new File(System.getProperty("user.home"), ".factual"), "factual-auth.yaml");
  private static final File AUTH = new File(new File(System.getProperty("user.home"), "factual"), "factual-auth.yaml");
  
  final double latitude = 34.06018;
  final double longitude = -118.41835;
  final int meters = 5000;

  @Before
  public void setup() {
    factual = factual();
  }

  @Test
  public void testSchema() {
    SchemaResponse schema = factual.schema("restaurants-us");
    assertTrue(schema.getTitle().toLowerCase().contains("restaurant"));
    assertTrue(schema.isGeoEnabled());
    assertTrue(schema.isSearchEnabled());

    assertFalse(schema.getColumnSchemas().isEmpty());

    ColumnSchema nameSchema = schema.getColumnSchema("name");
    assertEquals("name", nameSchema.name);
    assertEquals("String", nameSchema.datatype);
  }

  /**
   * Find rows in the global places database in the United States
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCoreExample1() {
    ReadResponse resp = factual.fetch(TABLE, new Query().field("country")
        .isEqual("US"));

    assertOk(resp);
    assertAll(resp, "country", "us");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);
  }

  /**
   * Find rows in the restaurant database whose name begins with "Star" and
   * return both the data and a total count of the matched rows.
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCoreExample2() {
    ReadResponse resp = factual.fetch(TABLE, new Query().field("name")
        .beginsWith("Star").includeRowCount());

    assertOk(resp);
    assertStartsWith(resp, "name", "Star");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("name", new HashMap() {
          {
            put("$bw", "Star");
          }
        });
      }
    }));
    params.put("include_count", true);
    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);
  }

  /**
   * Do a full-text search of the restaurant database for rows that match the
   * terms "Fried Chicken, Los Angeles"
   */
  @Test
  public void testCoreExample3() {
    ReadResponse resp = factual.fetch(TABLE,
        new Query().search("Fried Chicken, Los Angeles"));

    assertOk(resp);

    Map<String, Object> params = Maps.newHashMap();
    params.put("q", "Fried Chicken, Los Angeles");
    String respRaw = factual.get(FULL_TABLE, params);

    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * To support paging in your app, return rows 20-25 of the full-text search
   * result from Example 3
   */
  @Test
  public void testCoreExample4() {
    ReadResponse resp = factual.fetch(TABLE,
        new Query().search("Fried Chicken, Los Angeles").offset(20).limit(5));

    assertOk(resp);
    assertEquals(5, resp.getData().size());

    Map<String, Object> params = Maps.newHashMap();
    params.put("q", "Fried Chicken, Los Angeles");
    params.put("offset", 20);
    params.put("limit", 5);
    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(5, resp.getData().size());

  }

  @Test
  public void testWithinRectangle() {
    ReadResponse resp = factual.fetch(TABLE, new Query()
    .within(new Rectangle(34.06110, -118.42283, 34.05771, -118.41399)));

    assertOk(resp);
    assertTrue(resp.size() > 0);
  }

  @Test
  public void testNear() {
    ReadResponse resp = factual.fetch(TABLE, new Query()
    .near(new Point(34.06018, -118.41835)));

    assertOk(resp);
    assertTrue(resp.size() > 0);
  }

  /**
   * Return rows from the global places database with a name equal to "Stand"
   * within 5000 meters of the specified lat/lng
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCoreExample5() {
    ReadResponse resp = factual.fetch(
        TABLE,
        new Query().field("name").isEqual("Factual")
        .within(new Circle(latitude, longitude, meters)));
    assertNotEmpty(resp);
    assertOk(resp);

    Map<String, Object> params = Maps.newHashMap();
    params.put("geo", JsonUtil.toJsonStr(new HashMap() {
      {
        put("$circle", new HashMap() {
          {
            put("$center",
                new String[] { Double.toString(latitude),
                Double.toString(longitude) });
            put("$meters", meters);
          }
        });
      }
    }));
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("name", new HashMap() {
          {
            put("$eq", "Factual");
          }
        });
      }
    }));

    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testEncoding() {
    ReadResponse resp = factual.fetch(TABLE, new Query().field("website")
        .isEqual("http://www.papamurphys.com").limit(3));
    assertNotEmpty(resp);
    assertOk(resp);
    Map<String, Object> params = Maps.newHashMap();
    params.put("limit", 3);
    params.put("filters",
    "{\"website\":{\"$eq\":\"http://www.papamurphys.com\"}}");

    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testSort_byDistance() {

    ReadResponse resp = factual.fetch(
        TABLE,
        new Query().within(new Circle(latitude, longitude, meters)).sortAsc(
        "$distance"));

    assertNotEmpty(resp);
    assertOk(resp);
    assertAscendingDoubles(resp, "$distance");

    Map<String, Object> params = Maps.newHashMap();
    params.put("geo", JsonUtil.toJsonStr(new HashMap() {
      {
        put("$circle", new HashMap() {
          {
            put("$center",
                new String[] { Double.toString(latitude),
                Double.toString(longitude) });
            put("$meters", meters);
          }
        });
      }
    }));
    params.put("sort", "$distance:asc");

    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * {"$and":[{"name":{"$bw":"McDonald's"},"locality":{"$bw":"Los"}}
   * ]}
   */
  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testRowFilters_2beginsWith() {
    ReadResponse resp = factual.fetch(TABLE,
        new Query().field("name").beginsWith("McDonald's").field("locality")
        .beginsWith("Los"));

    assertOk(resp);
    assertStartsWith(resp, "name", "Mc");
    assertStartsWith(resp, "locality", "Los");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("$and", new Map[] { new HashMap() {
          {
            put("name", new HashMap() {
              {
                put("$bw", "McDonald's");
              }
            });
            put("locality", new HashMap() {
              {
                put("$bw", "Los");
              }
            });
          }
        } });
      }
    }));

    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testIn() {
    Query q = new Query().field("region").in("CA", "NM", "FL");
    ReadResponse resp = factual.fetch(TABLE, q);

    assertOk(resp);
    assertNotEmpty(resp);
    assertIn(resp, "region", "CA", "NM", "FL");

    q = new Query().field("region").inList(new ArrayList() {
      {
        add("CA");
        add("NM");
        add("FL");
      }
    });
    resp = factual.fetch(TABLE, q);
    assertOk(resp);
    assertNotEmpty(resp);
    assertIn(resp, "region", "CA", "NM", "FL");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("region", new HashMap() {
          {
            put("$in", new String[] { "CA", "NM", "FL" });
          }
        });
      }
    }));

    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);

  }

  /**
   * Tests a top-level AND with a nested OR and an $in:
   * 
   * <pre>
   * {$and:[
   *   {region:{$in:["MA","VT","NH"]}},
   *   {$or:[
   *     {name:{$bw:"Star"}},
   *     {name:{$bw:"Coffee"}}]}]}
   * </pre>
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testComplicated() {
    Query q = new Query();
    q.field("region").in("MA", "VT", "NH");
    q.or(q.field("name").beginsWith("Coffee"),
        q.field("name").beginsWith("Star"));

    ReadResponse resp = factual.fetch(TABLE, q);

    assertOk(resp);
    assertNotEmpty(resp);
    assertIn(resp, "region", "MA", "VT", "NH");

    // assert name starts with (coffee || star)
    for (String name : resp.mapStrings("name")) {
      assertTrue(name.toLowerCase().startsWith("coffee")
          || name.toLowerCase().startsWith("star"));
    }
  }

  private void assertIn(ReadResponse resp, String field, String... elems) {
    for (String val : resp.mapStrings(field)) {
      for (String elem : elems) {
        if (elem.equals(val)) {
          return;
        }
      }
      fail(val + " was not in " + Joiner.on(", ").join(elems));
    }
  }

  @Test
  public void testSimpleTel() {
    ReadResponse resp = factual.fetch(TABLE, new Query().field("tel")
        .beginsWith("(212)"));

    assertStartsWith(resp, "tel", "(212)");

    assertOk(resp);
  }

  /**
   * Search for places with names that have the terms "Fried Chicken"
   */
  @Test
  public void testFullTextSearch_on_a_field() {
    ReadResponse resp = factual.fetch(TABLE,
        new Query().field("name").search("Fried Chicken"));

    for (String name : resp.mapStrings("name")) {
      assertTrue(name.toLowerCase().contains("frie")
          || name.toLowerCase().contains("fry")
          || name.toLowerCase().contains("chicken"));
    }
  }

  @Test
  public void testCrosswalk_ex1() {
    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id").isEqual(
        "860fed91-3a52-44c8-af7b-8095eb943da1"));
    assertOk(resp);
    List<Map<String, Object>> crosswalks = resp.getData();
    assertFalse(crosswalks.isEmpty());
    assertFactualId(crosswalks, "860fed91-3a52-44c8-af7b-8095eb943da1");
  }

  @Test
  public void testCrosswalk_ex2() {
    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id")
        .isEqual("860fed91-3a52-44c8-af7b-8095eb943da1").field("namespace")
        .isEqual("citygrid"));
    List<Map<String, Object>> crosswalks = resp.getData();
    assertOk(resp);
    assertEquals(1, crosswalks.size());
    assertFactualId(crosswalks, "860fed91-3a52-44c8-af7b-8095eb943da1");
    assertNamespace(crosswalks, "citygrid");
  }

  @Test
  public void testCrosswalk_ex3() {
    ReadResponse resp = factual.fetch("crosswalk",
        new Query().field("namespace").isEqual("foursquare")
        .field("namespace_id").isEqual("4ae4df6df964a520019f21e3"));
    List<Map<String, Object>> crosswalks = resp.getData();
    assertOk(resp);
    assertFalse(crosswalks.isEmpty());
  }

  @Test
  public void testCrosswalk_limit() {
    ReadResponse resp = factual.fetch(
        "crosswalk",
        new Query().field("factual_id")
        .isEqual("860fed91-3a52-44c8-af7b-8095eb943da1").limit(1));
    List<Map<String, Object>> crosswalks = resp.getData();

    assertOk(resp);
    assertEquals(1, crosswalks.size());
  }

  @Test
  public void testResolve_ex1() {
    ResolveResponse resp = factual.fetch(
        PLACES_V3,
        new ResolveQuery().add("name", "McDonalds")
        .add("address", "10451 Santa Monica Blvd").add("region", "CA")
        .add("postcode", "90025"));
    assertOk(resp);
    assertNotEmpty(resp);
    assertTrue(resp.isResolved());
    assertTrue(resp.getResolved().get("name").equals("McDonald's"));
  }

  @Test
  public void testResolves() {
    ResolveResponse resp = factual.resolves(new ResolveQuery()
    .add("name", "McDonalds")
    .add("locality", "Los Angeles"));
    assertTrue(resp.getData().size() > 1);
  }

  @Test
  public void testMatch() {
    MatchQuery matchQuery = new MatchQuery().add("name", "McDonalds")
    .add("address", "10451 Santa Monica Blvd").add("region", "CA")
    .add("postcode", "90025").add("country", "us");
    String id = factual.match(PLACES_V3, matchQuery);
    assertTrue("c730d193-ba4d-4e98-8620-29c672f2f117".equals(id));
  }

  @Test
  public void testApiException_BadAuth() {
    Factual badness = new Factual("badkey", "badsecret");
    try {
      badness.fetch(TABLE, new Query().field("region").isEqual("CA"));
      fail("Expected to catch a FactualApiException");
    } catch (FactualApiException e) {
      assertEquals(401, e.getStatusCode());
      assertEquals("Unauthorized", e.getStatusMessage());
      assertTrue(e.getRequestUrl().startsWith(
      "http://api.v3.factual.com/t/places"));
    }
  }

  @Test
  public void testApiException_BadSelectField() {
    try {
      Query select = new Query().field("country").isEqual("US").only("hours");
      factual.fetch(TABLE, select);
      fail("Expected to catch a FactualApiException");
    } catch (FactualApiException e) {
      assertEquals(400, e.getStatusCode());
      assertTrue(e.getRequestUrl().startsWith(
      "http://api.v3.factual.com/t/places"));
      // verify the message includes useful info from the API error
      assertTrue(e.getMessage().contains("select"));
      assertTrue(e.getMessage().contains("unknown field"));
    }
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testSelect() {
    Query select = new Query().field("country").isEqual("US")
    .only("address", "country");
    assertEquals("[address, country]",
        Arrays.toString(select.getSelectFields()));

    ReadResponse resp = factual.fetch(TABLE, select);
    assertOk(resp);
    assertAll(resp, "country", "us");

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    params.put("select", "address,country");
    String respRaw = factual.get(FULL_TABLE, params);
    assertEquals(resp.getJson(), respRaw);

  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testCustomRead1() {

    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("region", new HashMap() {
          {
            put("$in", new String[] { "CA", "NM", "FL" });
          }
        });
      }
    }));
    params.put("select", "address,country");

    String respString = factual.get(FULL_TABLE, params);

    assertTrue(respString != null && respString.length() > 0);
  }

  @Test
  public void testCustomRead2() {
    Map<String, Object> params = Maps.newHashMap();
    params.put("select", "name,locality");
    params.put("include_count", true);

    String respString = factual.get(FULL_TABLE, params);
    assertTrue(respString != null && respString.length() > 0);
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testRawGet() {
    String respRaw = factual.get(
        FULL_TABLE,
        "filters="
        + UrlUtil.urlEncode("{\"name\" : {\"$eq\" : \"Starbucks\"} }"));
    try {
      JSONObject rootJsonObj = new JSONObject(respRaw);
      assertEquals("ok", rootJsonObj.get("status"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * And should not be used for geo queries. However, neither should it throw an
   * exception.
   */
  @Test
  public void testInvalidAnd() {
    Query q = new Query();
    q.and(q.field("locality").beginsWith("Los"),
        q.within(new Circle(latitude, longitude, meters)));

    ReadResponse resp = factual.fetch(TABLE, q);
    assertOk(resp);
  }

  @Test
  public void testFacet() {
    FacetQuery facet = new FacetQuery("region", "locality").search("Starbucks")
    .maxValuesPerFacet(20).minCountPerFacetValue(100).includeRowCount();

    FacetResponse resp = factual.fetch(TABLE, facet);
    assertOk(resp);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  public void testFacetFilter() {
    FacetQuery facet = new FacetQuery("locality").field("region").in("MA",
        "VT", "NH");
    facet.and(facet.or(facet.field("name").beginsWith("Coffee"),
        facet.field("name").beginsWith("Star")), facet.field("locality")
        .beginsWith("w"));
    FacetResponse resp = factual.fetch(TABLE, facet);
    assertOk(resp);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  public void testFacetGeo() {
    FacetQuery facet = new FacetQuery("locality").within(new Circle(latitude,
        longitude, meters));
    FacetResponse resp = factual.fetch(TABLE, facet);
    assertOk(resp);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  @Ignore
  public void testDiffs() {
    factual.setReadTimeout(1000 * 60);
    DiffsQuery diff = new DiffsQuery(1351728000000L);
    diff.before(1351900800000L);
    DiffsResponse resp = factual.fetch(PLACES_V3, diff);
  }

  @Test
  @Ignore
  public void testDiffsStreamOnlyStart() {
    factual.setReadTimeout(1000 * 5);
    DiffsQuery diff = new DiffsQuery();
    diff.after(1351728000000L);
    final FactualStream stream = factual.stream(PLACES_V3, diff, new DiffsCallback() {
      @Override
      public void onDiff(String line) {
        System.out.println(line);
      }
    });
    new Thread(new Runnable() {
      @Override
      public void run() {
        stream.start();
      }
    }).start();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    stream.end();
  }

  @Test
  @Ignore
  public void testDiffsStreamOnlyEnd() {
    factual.setReadTimeout(1000 * 5);
    DiffsQuery diff = new DiffsQuery();
    diff.before(1348619300964L);

    class DiffTestCb implements DiffsCallback {
      int count = 0;
      @Override
      public void onDiff(String line) {
        count++;
      }

      public int getCount() {
        return count;
      }
    }
    DiffTestCb cb = new DiffTestCb();
    FactualStream stream = factual.stream(PLACES_V3, diff, cb);
    stream.start();
  }

  @Test
  @Ignore
  public void testDiffsDate() {
    factual.setReadTimeout(1000 * 60);
    Calendar start = new GregorianCalendar();
    start.set(2012, 10, 1, 0, 0, 0);
    DiffsQuery diff = new DiffsQuery(start.getTime());
    Calendar end = new GregorianCalendar();
    end.set(2012, 10, 2, 0, 0, 0);
    diff.before(end.getTime());
    DiffsResponse resp = factual.fetch(PLACES_V3, diff);
  }

  @Test
  @Ignore
  public void testSubmitAdd() {
    Submit submit = new Submit()
    .setValue("longitude", 100)
    .setValue("name", "Test Location");
    SubmitResponse resp = factual.submit(SANDBOX_TABLE, submit,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertTrue(resp.isNewEntity());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testSubmitEdit() {
    Submit submit = new Submit().setValue("longitude", 100);
    SubmitResponse resp = factual.submit(SANDBOX_TABLE,
        "f33527e0-a8b4-4808-a820-2686f18cb00c", submit,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertFalse(resp.isNewEntity());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testSubmitDelete() {
    Submit submit = new Submit().removeValue("longitude");
    SubmitResponse resp = factual.submit(SANDBOX_TABLE,
        "f33527e0-a8b4-4808-a820-2686f18cb00c", submit,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertFalse(resp.isNewEntity());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testInsertAdd() {
    Insert insert = new Insert().setValue("longitude", 100);
    InsertResponse resp = factual.insert(SANDBOX_TABLE, insert,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertTrue(resp.isNewEntity());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testInsertEdit() {
    Insert insert = new Insert().setValue("longitude", 100);
    InsertResponse resp = factual.insert(SANDBOX_TABLE,
        "f33527e0-a8b4-4808-a820-2686f18cb00c", insert,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertFalse(resp.isNewEntity());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testInsertDelete() {
    Insert insert = new Insert().removeValue("longitude");
    InsertResponse resp = factual.insert(SANDBOX_TABLE,
        "f33527e0-a8b4-4808-a820-2686f18cb00c", insert,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertFalse(resp.isNewEntity());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testClear() {
    String factualId = "1d93c1ed-8cf3-4d58-94e0-05bbcd827cba";
    Clear clear = new Clear();
    clear.addField("longitude");
    clear.addField("latitude");
    ClearResponse resp = factual.clear(SANDBOX_TABLE, factualId, clear,
        new Metadata().user("test_driver_user"));
    assertOk(resp);
    assertEquals(factualId, resp.getFactualId());
    System.out.println("Commit id: "+resp.getCommitId());
  }

  @Test
  @Ignore
  public void testFlagClosed() {
    FlagResponse resp = factual.flagClosed(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    System.out.println(resp);
    assertOk(resp);
  }

  @Test
  @Ignore
  public void testFlagDuplicate() {
    FlagResponse resp = factual.flagDuplicate(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    System.out.println(resp);
    assertOk(resp);
  }

  @Test
  @Ignore
  public void testFlagInaccurate() {
    FlagResponse resp = factual.flagInaccurate(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore
  public void testFlagInappropriate() {
    FlagResponse resp = factual.flagInappropriate(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore
  public void testFlagNonExistent() {
    FlagResponse resp = factual.flagNonExistent(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore
  public void testFlagSpam() {
    FlagResponse resp = factual.flagSpam(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  @Ignore
  public void testFlagOther() {
    FlagResponse resp = factual.flagOther(SANDBOX_TABLE,
        "158294f8-3300-4841-9e49-c23d5d670d07",
        new Metadata().user("test_driver_user"));
    assertOk(resp);
  }

  @Test
  public void testGeopulse() {
    ReadResponse resp = factual.geopulse(new Geopulse(new Point(latitude,
        longitude)).only("area_statistics", "race_and_ethnicity"));
    List<Map<String, Object>> data = resp.getData();
    Map<String, Object> pulse = data.get(0);
    JSONObject demographics = (JSONObject) pulse.get("demographics");

    assertTrue(demographics.has("area_statistics"));
    assertTrue(demographics.has("race_and_ethnicity"));
    assertOk(resp);
  }

  @Test
  public void testGeocode() {
    ReadResponse resp = factual.reverseGeocode(new Point(latitude, longitude));
    assertTrue(resp.getData().size() == 1);
    assertOk(resp);
  }

  @Test
  public void testWorldGeographies() {
    Query query = new Query().and(
        new Query().field("name").isEqual("philadelphia"),
        new Query().field("country").isEqual("us"), new Query()
        .field("placetype").isEqual("locality"));
    ReadResponse resp = factual.fetch("world-geographies", query);
    assertTrue(resp.getData().size() == 14);
    assertOk(resp);
  }

  @Test
  public void testCPG() {
    // Search for products containing the word "shampoo"
    Query query = new Query().search("shampoo");
    ReadResponse resp = factual.fetch("products-cpg", query);
    assertOk(resp);

    // Same search as above, but filter the search results to include only the
    // brand "pantene"
    query = new Query().search("shampoo").field("brand").isEqual("pantene");
    resp = factual.fetch("products-cpg", query);
    assertOk(resp);

    // Same search as above, with added filter for products that are 12.6 oz.
    query = new Query().search("shampoo").field("brand").isEqual("pantene")
    .field("size").search("12.6 oz");
    resp = factual.fetch("products-cpg", query);
    assertOk(resp);

    // Search on UPC
    query = new Query().field("upc").isEqual("052000131512");
    resp = factual.fetch("products-cpg", query);
    assertTrue(resp.getData().size() == 1);
    assertOk(resp);

    // Find all beverages (filter by category)
    query = new Query().field("category").isEqual("beverages");
    resp = factual.fetch("products-cpg", query);
    assertOk(resp);

    // Count all beverage products
    query = new Query().field("category").isEqual("lip makeup").includeRowCount();
    resp = factual.fetch("products-cpg", query);
    assertOk(resp);
  }

  @Test
  public void testCPGCrosswalk() {

    // first, get the factual ID via UPC from the products table
    Query query = new Query().field("upc").isEqual("037000138006");
    ReadResponse resp = factual.fetch("products-cpg", query);
    Object factualId = resp.getData().get(0).get("factual_id");
    assertTrue(factualId != null);

    // next, call the crosswalk table using the factual ID
    query = new Query().field("factual_id").isEqual(factualId);
    resp = factual.fetch("products-crosswalk", query);
    assertTrue(resp.getData().size() > 0);
  }

  @Test
  public void testMulti() {
    MultiRequest multiRequest = new MultiRequest();
    multiRequest.addQuery("q1", TABLE, new Query().field("region").isEqual("CA"));
    multiRequest.addQuery("q2", TABLE, new Query().limit(1));
    MultiResponse multi = factual.sendRequests(multiRequest);
    Map<String, Response> data = multi.getData();
    assertTrue(data.size() == 2);

    Response resp = data.get("q1");
    assertTrue(resp.getIncludedRowCount() == 20);
    assertOk(resp);
    resp = data.get("q2");
    assertTrue(resp.getIncludedRowCount() == 1);
    assertOk(resp);
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testMultiRawRead() {
    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    MultiRequest multiReq = new MultiRequest();
    multiReq.addQuery("query1", TABLE, new FacetQuery("region", "locality"));
    multiReq.addQuery("query2", FULL_TABLE, params);
    MultiResponse multi = factual.sendRequests(multiReq);
    Map<String, Response> data = multi.getData();
    assertTrue(data.size() == 2);
    Response resp = data.get("query1");
    assertTrue(resp instanceof FacetResponse);
    resp = data.get("query2");
    assertTrue(resp instanceof RawReadResponse);
    assertTrue(resp.getIncludedRowCount() == 20);
    assertOk(resp);
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
  public void testRawReadMulti() {
    final Map<String, Object> queryParams1 = Maps.newHashMap();
    queryParams1.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("country", new HashMap() {
          {
            put("$eq", "US");
          }
        });
      }
    }));
    Map<String, Object> params = Maps.newHashMap();
    params.put("queries", JsonUtil.toJsonStr(new HashMap() {
      {
        put("q1", UrlUtil.toUrl("/t/places-v3", new HashMap() {
          {
            put("limit", 1);
          }
        }));
      }
    }));
    String respRaw = factual.get("multi", params);
    assertTrue(respRaw != null);
  }

  @Test
  public void testMultiComplex() {
    MultiRequest multiReq = new MultiRequest();
    multiReq.addQuery("q1", TABLE, new FacetQuery("region", "locality"));
    multiReq.addQuery("q2", TABLE, new Query().limit(10));
    multiReq.addQuery(
        "q3",
        PLACES_V3,
        new ResolveQuery().add("name", "McDonalds")
        .add("address", "10451 Santa Monica Blvd").add("region", "CA")
        .add("postcode", "90025"));
    MultiResponse multi = factual.sendRequests(multiReq);
    Map<String, Response> data = multi.getData();
    assertTrue(data.size() == 3);
    Response resp = data.get("q1");
    assertTrue(resp instanceof FacetResponse);
    assertOk(resp);
    resp = data.get("q2");
    assertTrue(resp instanceof ReadResponse);
    assertOk(resp);
    assertTrue(resp.getIncludedRowCount() == 10);
    resp = data.get("q3");
    assertTrue(resp instanceof ReadResponse);
    assertOk(resp);
  }

  @Test
  public void testMultiCrosswalk() {
    MultiRequest multiReq = new MultiRequest();
    multiReq.addQuery(
        "q",
        "crosswalk",
        new Query().field("factual_id")
        .isEqual("97598010-433f-4946-8fd5-4a6dd1639d77").limit(1));
    MultiResponse multi = factual.sendRequests(multiReq);
    for (Response resp : multi.getData().values()) {
      assertOk(resp);
    }
  }

  @Test
  public void testMultiGeopulseWithNearestAddress() {
    MultiRequest multiReq = new MultiRequest();
    multiReq.addQuery("q1", new Geocode(new Point(latitude, longitude)));
    multiReq.addQuery("q2", new Geopulse(new Point(latitude, longitude)));
    MultiResponse multi = factual.sendRequests(multiReq);
    assertTrue(multi.getData().size() == 2);
    for (Response resp : multi.getData().values()) {
      assertOk(resp);
    }
  }

  @Test
  public void testMultiGeopulseWithNearestPlace() {
    MultiRequest multiReq = new MultiRequest();
    multiReq.addQuery("q1", "global",
        new Query().within(new Circle(latitude, longitude, meters)));
    multiReq.addQuery("q2", new Geopulse(new Point(latitude, longitude)));
    MultiResponse multi = factual.sendRequests(multiReq);
    assertTrue(multi.getData().size() == 2);
    for (Response resp : multi.getData().values()) {
      assertOk(resp);
    }
  }

  /**
   * Test debug mode
   */
  @Test
  public void testDebug() {
    factual.debug(true);
    ReadResponse resp = factual.fetch(TABLE, new Query().field("country")
        .isEqual("US"));
    factual.debug(false);
    assertOk(resp);
    assertAll(resp, "country", "us");
  }

  @Test
  public void testBasicUnicode() throws UnsupportedEncodingException {
    ReadResponse resp = factual.fetch("global", new Query().field("locality")
        .isEqual("大阪市").limit(5)); // Osaka
    assertOk(resp);
    assertTrue(resp.getData().size() == 5);
    assertTrue(Arrays.equals(
        ((String) resp.first().get("locality")).getBytes(),
        ("大阪市".getBytes("UTF-8"))));

  }

  @Test
  public void testMultiUnicode() throws UnsupportedEncodingException {
    Map<String, Object> params = Maps.newHashMap();
    params.put("filters", JsonUtil.toJsonStr(new HashMap() {
      {
        put("locality", new HashMap() {
          {
            put("$eq", "בית שמש"); // Locality in Israel
          }
        });
      }
    }));
    MultiRequest multiReq = new MultiRequest();
    multiReq.addQuery("q1", FULL_TABLE, params);
    multiReq.addQuery("q2", TABLE,
        new Query().field("locality").isEqual("München").limit(10)); // Munich,
    // Germany
    multiReq.addQuery(
        "q3",
        PLACES_V3,
        new ResolveQuery().add("name", "César E. Chávez Library")
        .add("locality", "Oakland").add("region", "CA")
        .add("address", "3301 E 12th St"));
    MultiResponse multi = factual.sendRequests(multiReq);
    assertTrue(multi.getData().size() == 3);
    Response resp = multi.getData().get("q1");
    assertOk(resp);
    resp = multi.getData().get("q2");
    assertTrue(resp.getIncludedRowCount() == 10);
    assertTrue(resp instanceof ReadResponse);
    assertTrue(Arrays.equals(
        ((String) ((ReadResponse) resp).first().get("locality")).getBytes(),
        "München".getBytes("UTF-8")));
    resp = multi.getData().get("q3");
    assertTrue(resp instanceof ResolveResponse);
    assertTrue(((ResolveResponse) resp).getResolved().get("tel")
        .equals("(510) 535-5620"));
  }

  @Test
  public void testOffsetZero() {
    Query query = new Query();
    query.limit(50).offset(0);
    ReadResponse r = factual.fetch("monetize-data", query);
    assertOk(r);
  }

  @Test
  public void testFetchRow1() {
    String factualId = "0000022c-4ab3-4f5d-8e67-6a6ff1826a93";
    RowResponse resp = factual.fetchRow("places", factualId);
    assertOk(resp);
    assertTrue(factualId.equals(resp.getRowData().get("factual_id")));
    assertFalse(resp.isDeprecated());
  }

  @Test
  public void testFetchRow2() {
    String factualId = "0000022c-4ab3-4f5d-8e67-6a6ff1826a93";
    RowResponse resp = factual.fetchRow("places", factualId, new RowQuery().only("name"));
    assertOk(resp);
    assertTrue("Icbm".equals(resp.getRowData().get("name")));
    assertFalse(resp.isDeprecated());
  }

  @Test
  public void testIncludes1() throws JSONException {
    int categoryId = 10;
    Query q = new Query().field("category_ids").includes(categoryId);
    ReadResponse resp = factual.fetch(TABLE, q);
    assertOk(resp);
    for (Map<String, Object> data : resp.getData()) {
      JSONArray categoryIds = (JSONArray) data.get("category_ids");
      boolean found = false;
      for (int i=0;i<categoryIds.length();i++) {
        if (categoryIds.getInt(i) == categoryId) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void testIncludes2() throws JSONException {
    List<Object> categoryIds = Lists.newArrayList();
    categoryIds.add(10);
    categoryIds.add(120);
    Query q = new Query().field("category_ids").includesAnyList(categoryIds);
    ReadResponse resp = factual.fetch(TABLE, q);
    assertOk(resp);

    for (Map<String, Object> data : resp.getData()) {
      boolean found = false;
      for (Object categoryId : categoryIds) {
        JSONArray rowCategoryIds = (JSONArray) data.get("category_ids");
        for (int i=0;i<rowCategoryIds.length();i++) {
          if (((Integer)rowCategoryIds.get(i)).intValue() == ((Integer) categoryId).intValue()) {
            found = true;
            break;
          }
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void testBoost() {
    BoostResponse response = factual.boost(TABLE, "03c26917-5d66-4de9-96bc-b13066173c65", "Local Business Data, Global", "test_driver_user");
    assertOk(response);
  }

  @Test
  public void testBoostNoUser() {
    BoostResponse response = factual.boost(TABLE, "03c26917-5d66-4de9-96bc-b13066173c65", "Local Business Data, Global");
    assertOk(response);
  }

  @Test
  public void testBoostNoUserNoSearch() {
    BoostResponse response = factual.boost(TABLE, "03c26917-5d66-4de9-96bc-b13066173c65");
    assertOk(response);
  }

  @Test
  public void testBoostObject() {
    Boost boost = new Boost("03c26917-5d66-4de9-96bc-b13066173c65");
    boost.search("Local Business Data, Global");
    boost.user("test_driver_user");
    BoostResponse response = factual.boost(TABLE, boost);
    assertOk(response);
  }

  @Test
  public void testBoostExistingQuery() {
    Query query = new Query().search("Local Business Data, Global").user("test_driver_user");
    BoostResponse response = factual.boost("places", "03c26917-5d66-4de9-96bc-b13066173c65", query);
    assertOk(response);
  }

  @Test
  public void testHeaders() {
    ReadResponse resp = factual.fetch(TABLE, new Query().field("country")
        .isEqual("US"));

    HttpHeaders headers = resp.getRawResponse().getHeaders();
    assertOk(resp);
    assertEquals("application/json; charset=utf-8", headers.getContentType());
  }

  private void assertFactualId(List<Map<String, Object>> crosswalks, String id) {
    for (Map<String, Object> cw : crosswalks) {
      assertEquals(id, cw.get("factual_id"));
    }
  }

  private void assertNamespace(List<Map<String, Object>> crosswalks, String ns) {
    for (Map<String, Object> cw : crosswalks) {
      assertEquals(ns, cw.get("namespace"));
    }
  }

  private static final void assertNotEmpty(Response resp) {
    assertFalse(resp.isEmpty());
  }

  private static final void assertOk(Response resp) {
    assertEquals("ok", resp.getStatus());
  }

  private void assertAll(ReadResponse resp, String field, String expected) {
    for (String out : resp.mapStrings(field)) {
      assertEquals(expected, out);
    }
  }

  private void assertStartsWith(ReadResponse resp, String field, String substr) {
    for (String out : resp.mapStrings(field)) {
      assertTrue(out.startsWith(substr));
    }
  }

  private void assertAscendingDoubles(ReadResponse resp, String field) {
    Double prev = Double.MIN_VALUE;
    for (Map<?, ?> rec : resp.getData()) {
      Double d = (Double) rec.get(field);
      assertTrue(d >= prev);
      prev = d;
    }
  }

  private static Map loadMapFromYaml(File file) {
    InputStream is = null;
    try {
      is = FileUtils.openInputStream(file);
      return (Map) (new Yaml()).load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      Closeables.closeQuietly(is);
    }
  }

  public static Factual factual() {
	
	  //TODO
		System.getProperties().put("http.proxyHost", "172.28.184.18");
		System.getProperties().put("http.proxyPort", "8080");
	  
    if (!AUTH.exists()) {
      fail("You must provide " + AUTH);
      System.err.println("You must provide " + AUTH);
      throw new IllegalStateException("Could not find " + AUTH);
    } else {
      Map auth = loadMapFromYaml(AUTH);
      return new Factual((String) auth.get("key"), (String) auth.get("secret"));
    }
  }

}
