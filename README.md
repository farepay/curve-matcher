# Curve Matcher

[![Build Status](https://www.travis-ci.org/farepay/curve-matcher.svg?branch=master)](https://www.travis-ci.org/farepay/curve-matcher)

A Kotlin library for doing curve matching with Fréchet distance and Procrustes analysis. It has been ported from Javascript library [Curve Matcher](https://github.com/chanind/curve-matcher).

## Installation

Add JitPack to the list of your Gradle repositories

```
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

Add the dependency

```
dependencies {
    implementation 'com.github.farepay:curve-matcher:v1.1.1-kotlin'
}
```

For Maven and sbt see [here](https://jitpack.io/#farepay/curve-matcher).

## Getting started

The core of `curve-matcher` is a function called `Curve$similarityTo` which estimates how similar the shapes of 2 curves are to each other, returning a value between `0` and `1`.

![shapeSimilarity example curves](http://misc-cdn-assets.s3-us-west-2.amazonaws.com/shape_similarity.png)

Curves are lists of points of `x` and `y` like below:

```kotlin
val curve1 = Curve(
    Point(0.0, 0.0),
    Point(4.0, 4.0)
)
```

calculating similarity between 2 curves is as simple as calling:

```kotlin
// 1 means identical shape, 0 means very different shapes
val similarity = curve1.similarityTo(curve2)
```

`Curve$similarityTo` automatically adjusts for rotation, scale, and translation differences between, so it doesn't matter if the curves are different sizes or in different locations on the screen - as long as they have the same shape the similarity score will be close to `1`.

You can further customize the accuracy of the `Curve$similarityTo` function by changing `estimationPoints` (default 50) and `rotations` (default 10). Increasing these will improve accuracy, but the function will take longer to run.

```kotlin
// higher accuracy, but slower
curve1.similarityTo(curve2, estimationPoints = 200, rotations = 30 )

// lower accuracy, but faster
curve1.similarityTo(curve2, estimationPoints = 10, rotations = 0 )
```

You can also restrict the range of rotations that are checked using the `restrictRotationAngle` option. This option means the `Curve$similarityTo` function will only check rotations within +- `restrictRotationAngle` radians. If you'd like to disable rotation correction entirely, you can set `checkRotations = false`. These are shown below:

```kotlin
// Only check rotations between -0.1 π to 0.1 π
curve1.similarityTo(curve2, restrictRotationAngle = 0.1 * Math.PI)

// disable rotation correction entirely
curve1.similarityTo(curve2, checkRotations = false)
```

## How it works

Internally, `Curve$similarityTo` works by first normalizing the curves using [Procrustes analysis](https://en.wikipedia.org/wiki/Procrustes_analysis) and then calculating [Fréchet distance](https://en.wikipedia.org/wiki/Fr%C3%A9chet_distance) between the curves.

Procrustes analysis attempts to translate both the curves to the origin and adjust their scale so they're the same size. Then, it rotates the curves so their rotations are as close as possible.

In practice, Procrustes analysis has 2 issues which curve-matcher works to address.
First, it's very dependent on how the points of the curve are spaced apart from each other. To account for this, `Curve$similarityTo` first redraws each curve using 50 (by default) points equally spaced out along the length of the curve. In addition, Procrustes analysis sometimes doesn't choose the best rotation if curves are not that similar to each other, so `Curve$similarityTo` also tries 10 (by default) equally spaced rotations to make sure it picks the best possible rotation normalization. You can adjust these parameters via the `estimationPoints` and `rotations` options to `Curve$similarityTo`.

If you'd like to implement your own version of `Curve$similarityTo` there's a number of helper methods that are exported by `curve-matcher` which you can use as well, discussed below:

## Fréchet distance

Curve matcher includes an implemention of a discreet Fréchet distance algorithm from the paper [Computing Discrete Fréchet Distance](http://www.kr.tuwien.ac.at/staff/eiter/et-archive/cdtr9464.pdf). You can use this function by passing in 2 curves, as below:

```kotlin
val dist = curve1.distance(curve2)
```

A caveat of discreet Fréchet distance is that the calculation is only as accurate as the length of the line segments of the curves. That means, if curves have long distances between each of the points in the curve, or if there's not many points in the curve, the calculation may be inaccurate. To help alleviate this, Curve matcher provides a helper method called `Curve$subdivide` which takes a curve and splits up line segments in the curve to improve the accuracy of the Fréchet distance calculation. This can be used as below:

```kotlin
// subdivide the curves so each segment is at most length 0.5
val dividedCurve1 = curve1.subdivide(maxLen = 0.5)
val dividedCurve2 = curve2.subdivide(maxLen = 0.5)

// now, the frechet distance is guaranteed to be at most off by 0.5
val dist = dividedCurve1.distance(dividedCurve2)
```

## Procrustes analysis

Curve matcher also exports a few methods to help with Procrustes analysis. However, before running these it's recommended that curves be rebalanced so that the points of the curve are all equally spaced along its length. This can be done with a function called `rebalance` as below:

```kotlin
// redraw the curve using 50 equally spaced points
val balancedCurve = curve.rebalance(numPoints = 50)
```

Then, to normalize scale and translation, pass the curve into `Curve$normalize` as below:

```kotlin
val balancedCurve = curve.rebalance(numPoints)
val scaledAndTranslatedCurve = balancedCurve.normalize()
```

You can read more about these algorithms here: https://en.wikipedia.org/wiki/Procrustes_analysis

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).
