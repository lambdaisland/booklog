# Booklog

Demo application which featured in Lambda Island episodes

* [28. Buddy Authentication](https://lambdaisland.com/episodes/buddy-authentication)
* [34. Acceptance Testing with SparkleDriver](https://lambdaisland.com/episodes/acceptance-testing-sparkledriver)

This app lets you keep a log of the books you read. It's web stack consists of
Ring/Compojure/Hiccup, with Buddy for authentication,
[Spicerack](https://github.com/jackrusher/spicerack] for persistence, and
[SparkleDriver](https://github.com/jackrusher/sparkledriver) for integration
testing.

## Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(go)
```

This start the app system, including database and web server, available at
[http://localhost:1234](http://localhost:1234). Although this is not a rich
client app (it doesn't use ClojureScript), it still uses Figwheel to provide hot
reloading of CSS (written using Garden).

## Testing

To run the tests, use

``` shell
lein test
```

## Deploying to Heroku

This assumes you have a
[Heroku account](https://signup.heroku.com/dc), have installed the
[Heroku toolbelt](https://toolbelt.heroku.com/), and have done a
`heroku login` before.

``` sh
git init
git add -A
git commit
heroku create
git push heroku master:master
heroku open
```

## Running with Foreman

Heroku uses [Foreman](http://ddollar.github.io/foreman/) to run your
app, which uses the `Procfile` in your repository to figure out which
server command to run. Heroku also compiles and runs your code with a
Leiningen "production" profile, instead of "dev". To locally simulate
what Heroku does you can do:

``` sh
lein with-profile -dev,+production uberjar && foreman start
```

Now your app is running at
[http://localhost:5000](http://localhost:5000) in production mode.

## License

Copyright © 2017 Arne Brasseur

Distributed under the Mozilla Public License 2.0 https://www.mozilla.org/en-US/MPL/2.0/

## Chestnut

Created with [Chestnut](http://plexus.github.io/chestnut/) 0.15.0-SNAPSHOT (242699d0).
