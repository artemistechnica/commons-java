# commons-java

### Data types

###### Either

An `Either` is a data structure capturing exactly one of two specified types; _either_ `A` _or_ `B` but never both.

An `Either` is also a right-biased data structure; meaning a function is only applied to an `Either's` value _iff_ that value is defined in the _right_ position.

**Simple Example**

```java
// Instantiate a simple explicitly typed [[Either]]
Either<String, Integer> x0 = Either.<String, Integer>right(1);
// Instantiate a less verbose simple typed [[Either]]
Either<String, Integer> x0 = Either.right(1); // x0.right == 1
// Apply a function to an Either's right value
Either<String, Integer> x1 = x0.map(i -> i + 1); // x1.right == 2
// Apply a function that also returns an Either
Either<String, String> x2 = x1.flatMap(i -> Either.right(Integer.toString(i))); // x2.right == "2"
```

The following will not apply the function because the `Either's` value is in the _left_ position. This is understood to be in an erroneous state and allows for short circuting.

```java
Either<String, String> e0 = Either.left("ERROR");
Either<String, Integer> e1 = e0.map(i -> 42); // e1.left == "ERROR"
```