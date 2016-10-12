package cosmos.actiongraph.simulator

import aianash.commons.events._

//
object GoFro extends Website {

  val locchoices =
    singlechoices("bangalore", "delhi", "hyderabad")
      .stateful()

  page("home-page",
    props = props(
      "location" -> locchoices
    ),
    section("collections",
      noprops,
      noaction,
      element("romantic",
        action = GoToPage("romantic", params = props("location" -> locchoices)),
        props = props(
          "collection" -> "romantic",
          "trips" -> 127
        )
      ),
      element("beaches",
        action = GoToPage("beaches", params = props("location" -> locchoices)),
        props = props(
          "collection" -> "beaches",
          "trips" -> 118
        )
      ),
      element("adventure",
        action = GoToPage("adventure", params = props("location" -> locchoices)),
        props = props(
          "collection" -> "adventure",
          "trips" -> 33
        )
      ),
      element("bachelor-bash",
        action = GoToPage("bachelor-bash", params = props("location" -> locchoices)),
        props = props(
          "collection" -> "bachelor-bash",
          "trips" -> 7
        )
      )
    ),
    section("why-travel-with-us",
      noprops,
      noaction,
      element("best-price",
        action = GoToSection("best-price"), noprops),
      element("customization",
        action = GoToSection("customization"), noprops),
      element("booking-with-ease",
        GoToSection("booking-with-ease"), noprops),
      element("plentiful-choices",
        action = GoToSection("plentiful-choices"), noprops)
    ),
    section("best-price",
      props = props(
        "keywords" -> multichoices("marketplace-model", "trusted-supplier", "best-price", "competitive")),
      noaction
    ),
    section("customization",
      props = props(
        "keywords" -> multichoices("real-time", "end-to-end-customization", "smart-tool")),
      noaction
    ),
    section("booking-with-ease",
      props = props(
        "keywords" -> multichoices("real-time", "expert-assist", "live-chat")),
      noaction
    ),
    section("plentiful-choices",
      props = props(
        "keywords" -> multichoices("trusted-network", "get-what-you-want")),
      noaction
    ),
    section("how-it-works",
      props = props(
        "keywords" -> multichoices("favorite-holiday-package", "holiday-planner", "pay-and-book", "one-place")),
      noaction
    ),
    section("about-us",
      noprops,
      action = GoToPage("about-us")
    ),
    section("contact-us",
      noprops,
      action = GoToPage("contact-us")
    )
  )

  page("romantic",
    noprops,
    section("filters",
      noprops,
      noaction,
      element("budget",
        action = Click(props("budget" -> rangechoices(17986, 253233))), noprops),
      element("hotels",
        action = Click(props("hotels" -> singlechoices("2-star", "3-star", "4-star", "5-star"))), noprops),
      element("destinations",
        action = Click(props("destinations" -> multichoices("mauritius", "bangkok", "krabi", "kuala-lumpur"))), noprops),
      element("themes",
        action = Click(props("themes" -> multichoices("beaches", "city-breaks", "adventure", "family"))), noprops),
      element("duration",
        action = Click(props("nights" -> rangechoices(3, 9))), noprops)
    ),
    section("listing",
      noprops,
      noaction,
      element("bali",
        action = AddToCart(),
        props = props(
          "place" -> "Kuta,Indonesia",
          "duration" -> "4 night",
          "theme" -> multichoices("honeymoon", "couple", "romantic"),
          "price" -> 17986,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("bangkok",
        action = AddToCart(),
        props = props(
          "place" -> "Bangkok,Thailand",
          "duration" -> "3 night",
          "theme" -> multichoices("romantic", "couple"),
          "price" -> 22947,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("bangkok-5-star",
        action = AddToCart(),
        props = props(
          "place" -> "Bangkok,Thailand",
          "duration" -> "3 night",
          "theme" -> multichoices("weekend-getaways", "couple", "romantic"),
          "price" -> 25743,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("godwin-hotel",
        action = AddToCart(),
        props = props(
          "place" -> "Goa,India",
          "duration" -> "3 night",
          "theme" -> multichoices("couple", "romantic"),
          "price" -> 26125,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("dubai",
        action = AddToCart(),
        props = props(
          "place" -> "Dubai,UAE",
          "duration" -> "3 night",
          "theme" -> multichoices("couple", "romantic"),
          "price" -> 27274,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("mauritius",
        action = AddToCart(),
        props = props(
          "place" -> "Mauritius,Mauritius",
          "duration" -> "4 night",
          "theme" -> multichoices("couple", "romantic", "beaches", "city-breaks"),
          "price" -> 31180,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        ))
    )
  )

  page("beaches",
    noprops,
    section("filters",
      noprops,
      noaction,
      element("budget",
        action = Click(props("budget" -> rangechoices(17172, 253528))), noprops),
      element("hotels",
        action = Click(props("hotels" -> singlechoices("2-star", "3-star", "4-star", "5-star"))), noprops),
      element("destinations",
        action = Click(props("destinations" -> multichoices("mauritius", "bangkok", "krabi", "kuala-lumpur"))), noprops),
      element("themes",
        action = Click(props("themes" -> multichoices("romantic", "city-breaks", "fun-with-friends", "family"))), noprops),
      element("duration",
        action = Click(props("nights" -> rangechoices(4, 9))), noprops)
    ),
    section("listing",
      noprops,
      noaction,
      element("bali",
        action = AddToCart(),
        props = props(
          "place" -> "Kuta,Indonesia",
          "duration" -> "4 night",
          "theme" -> multichoices("beaches", "family", "friends", "couple", "solo"),
          "price" -> 17986,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("pattaya",
        action = AddToCart(),
        props = props(
          "place" -> "Pattaya,Thailand",
          "duration" -> "1 night",
          "theme" -> multichoices("beaches", "solo"),
          "price" -> 21636,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("mauritius",
        action = AddToCart(),
        props = props(
          "place" -> "Mauritius,Mauritius",
          "duration" -> "4 night",
          "theme" -> multichoices("couple", "romantic", "beaches", "city-breaks", "honeymoon"),
          "price" -> 29621,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("krabi",
        action = AddToCart(),
        props = props(
          "place" -> "Krabi,Thailand",
          "duration" -> "5 night",
          "theme" -> multichoices("couple", "beaches", "romantic"),
          "price" -> 32193,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("nusa-dua",
        action = AddToCart(),
        props = props(
          "place" -> "Nusa Dua,Indonesia",
          "duration" -> "4 night",
          "theme" -> multichoices("beaches", "family", "friends"),
          "price" -> 34428,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("langkawi",
        action = AddToCart(),
        props = props(
          "place" -> "Langkawi,Malaysia",
          "duration" -> "6 night",
          "theme" -> multichoices("family", "kids", "beaches"),
          "price" -> 37653,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        ))
    )
  )

  page("adventure",
    noprops,
    section("filters",
      noprops,
      noaction,
      element("budget",
        action = Click(props("budget" -> rangechoices(12364, 136317))), noprops),
      element("hotels",
        action = Click(props("hotels" -> singlechoices("3-star", "4-star", "5-star"))), noprops),
      element("destinations",
        action = Click(props("destinations" -> multichoices("siem-reap", "bangkok", "dubai", "kuala-lumpur"))), noprops),
      element("themes",
        action = Click(props("themes" -> multichoices("romantic", "city-breaks", "fun-with-friends", "family"))), noprops),
      element("duration",
        action = Click(props("nights" -> rangechoices(2, 7))), noprops)
    ),
    section("listing",
      noprops,
      noaction,
      element("phuket",
        action = AddToCart(),
        props = props(
          "place" -> "Phuket,Thailand",
          "duration" -> "3 night",
          "theme" -> multichoices("family", "friends", "adventure"),
          "price" -> 12364,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("kuala-lumpur",
        action = AddToCart(),
        props = props(
          "place" -> "Kuala Lumpur,Malaysia",
          "duration" -> "3 night",
          "theme" -> multichoices("city-breaks", "friends", "adventure"),
          "price" -> 24117,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("bali",
        action = AddToCart(),
        props = props(
          "place" -> "Kuta,Indonesia",
          "duration" -> "5 night",
          "theme" -> multichoices("couple", "adventure", "friends", "budget-trips", "solo"),
          "price" -> 27879,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("kerala",
        action = AddToCart(),
        props = props(
          "place" -> "Kerala,India",
          "duration" -> "5 night",
          "theme" -> multichoices("adventure", "friends"),
          "price" -> 43584,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("cambodia",
        action = AddToCart(),
        props = props(
          "place" -> "Cambodia,Thailand",
          "duration" -> "5 night",
          "theme" -> multichoices("solo", "family", "friends", "adventure"),
          "price" -> 44039,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("singapore",
        action = AddToCart(),
        props = props(
          "place" -> "Singapore,Singapore",
          "duration" -> "4 night",
          "theme" -> multichoices("adventure", "solo", "weekend-getaways", "budget-trips"),
          "price" -> 45839,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        ))
    )
  )

  page("bachelor-bash",
    noprops,
    section("filters",
      noprops,
      noaction,
      element("budget",
        action = Click(props("budget" -> rangechoices(13787, 110820))), noprops),
      element("hotels",
        action = Click(props("hotels" -> singlechoices("3-star", "4-star", "5-star"))), noprops),
      element("destinations",
        action = Click(props("destinations" -> multichoices("singapore", "seminyak", "kuta", "pattaya"))), noprops),
      element("themes",
        action = Click(props("themes" -> multichoices("fun-with-friends"))), noprops),
      element("duration",
        action = Click(props("nights" -> rangechoices(3, 4))), noprops)
    ),
    section("listing",
      noprops,
      noaction,
      element("bali",
        action = AddToCart(),
        props = props(
          "place" -> "Kuta,Indonesia",
          "duration" -> "3 night",
          "theme" -> multichoices("bachelor-bash", "friends", "budget-trips", "fun-with-friends"),
          "price" -> 13787,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("singapore",
        action = AddToCart(),
        props = props(
          "place" -> "Singapore,Singapore",
          "duration" -> "4 night",
          "theme" -> multichoices("fun-with-friends", "friends", "most-popular", "bachelor-bash", "budget-trips"),
          "price" -> 48248,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("seminyak",
        action = AddToCart(),
        props = props(
          "place" -> "Seminyak,Indonesia",
          "duration" -> "3 night",
          "theme" -> multichoices("fun-with-friends", "friends", "bachelor-bash"),
          "price" -> 25772,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        )),
      element("pattaya",
        action = AddToCart(),
        props = props(
          "place" -> "Pattaya,Thailand",
          "duration" -> "4 night",
          "theme" -> multichoices("bachelor-bash", "friends", "fun-with-friends"),
          "price" -> 110820,
          "nperson" -> 2,
          "includes" -> multichoices("hotel", "flight", "things-to-do")
        ))
    )
  )

  page("about-us",
    noprops,
    section("team",
      props = props("job" -> "ceo"),
      noaction
    ),
    section("feedback",
      noprops,
      action = Submit(props("feedback" -> "email"))
    ),
    section("address",
      noprops,
      action = Click(props("type" -> singlechoices("email", "phone")))
    )
  )

  page("contact-us",
    noprops,
    section("address", noprops, noaction),
    section("queries",
      noprops,
      noaction,
      element("sales-queries",
        action = Click(props("type" -> singlechoices("phone", "email"))),
        props = noprops),
      element("marketing-queries",
        action = Click(props("type" -> singlechoices("phone", "email"))),
        props = noprops)
    )
  )
}