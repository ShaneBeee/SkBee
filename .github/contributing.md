# Contributing to SkBee

## Golden Rules:

- I would prefer to keep NMS related to code to an absolutel minimum.
- Please make sure Skript doesn't already have an element before adding it to SkBee:
  - Check Skript's docs
  - Check Skript's `dev/feature` branch to see if anything has been added
  - Check Skript's PRs
- Do not copy/paste code from other addons/sources.
All code shoudld be written by you and adhere to our [license](https://github.com/ShaneBeee/SkBee/blob/master/LICENSE)

## Before:
Before taking the time to make a big change and submitting a PR, we recommend posting a suggestion on the [Issue Tracker](https://github.com/ShaneBeee/SkBee/issues) outlining what you'd like to do. This way the team can discuss with you whether or not we want this in SkBee.

## ClassInfos:
- All class infos registered should have a check if the info already exists and a log warning if it does. Example:
```java
if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
    // Register ClassInfo
} else {
    Util.logLoading("It looks like another addon registered 'spell' already.");
    Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
}
```

## Tests:

### Single Element:
When adding a new element please create a test for all possible casses.
If you are adding a single element, put the test in the appropriate `test/scripts/elements/<module>/<type>` directory.

- `module` = The Directory under `.../skbee/elements/`
- `<type>` = The type of element (expressions/conditions/effects/ect.)

### Modules/Grouped Elements:
If you are adding a bunch of elements that can be grouped into one test, you can plop it in th `test/scripts/genera/` directory.
See `test/scripts/general/text-component.sk` as an example.

### Naming:
All test names should start with "SkBee - ".
This helps distinguish the tests from Skript's tests in a GitHub action.
