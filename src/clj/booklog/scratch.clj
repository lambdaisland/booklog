(require '[buddy.hashers :as hashers])

(hashers/derive "trustno1")
;;=> "bcrypt+sha512$98aef2f4a00737a66c06fba60e2573bc$12$ec97183d2d3c374d53f55a9feb591e468d60164698571283"

(str/split (hashers/derive "trustno1") #"\$")
;;=> ["bcrypt+sha512"                                    ; encryption
;;    "5f8dc902b11de2aa17e67ecb0f76713f"                 ; salt
;;    "12"                                               ; iterations
;;    "622891a3aed90cea118b3ed791b96e27818389bddf9e5a9b" ; hashed password
;;    ]


(hashers/check "trustno1" (hashers/derive "trustno1"))
;;=> true
