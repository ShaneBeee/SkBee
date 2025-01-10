# Contributing to SkBee

## Golden Rules:

- Keep any/all NMS/Reflection related code to an absolute minimum.
- Please make sure Skript doesn't already have an element before adding it to SkBee:
  - Check Skript's docs
  - Check Skript's `dev/feature` branch to see if anything has been added
  - Check Skript's PRs
- Do not copy/paste code from other addons/sources.
All code shoudld be written by you and adhere to our [license](https://github.com/ShaneBeee/SkBee/blob/master/LICENSE)

## Before:
Before taking the time to make a big change and submitting a PR, we recommend posting a suggestion on the [Issue Tracker](https://github.com/ShaneBeee/SkBee/issues) outlining what you'd like to do. This way the team can discuss with you whether or not we want this in SkBee.

## DOs:
- PRs should be code based
- Code changes/additions should follow the style of SkBee and not conventions Skript has set.

## DON'Ts:
- We won't accept PRs that are just string based changes. Your PR contribution should be code based. (If you find a typo, report it and/or let one of the team members know.)
- If a class seems outdated (in terms of formatting) please do not reformat the entire class for small changes (it makes PRs really difficult to read).
- Don't break any current syntaxes (ie: removing/changing a pattern with a breaking change).

## ClassInfos:
- All ClassInfos registered should have a check if the ClassInfo already exists and a log warning if it does.    (This helps prevent clashing with addons)
Example:
```java
if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
    // Register ClassInfo
} else {
    Util.logLoading("It looks like another addon registered 'spell' already.");
    Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
}
```

## Branches:
PRs should be done to the `dev/update` branch.
Changes are done to this branch before merged to `master` on release.
If you forget to point your PR towards the update branch... don't worry, you can change it or a team member can change it.

## Tests:

### Single Element:
When adding a new element please create a test for all possible casses.
If you are adding a single element, put the test in the appropriate `test/scripts/elements/<module>/<type>` directory.

- `module` = The Directory under `.../skbee/elements/`
- `<type>` = The type of element (expressions/conditions/effects/ect.)

### Modules/Grouped Elements:
If you are adding a bunch of elements that can be grouped into one test, you can plop it in th `test/scripts/general/` directory.
See `test/scripts/general/text-component.sk` as an example.

### Naming:
All test names should start with "SkBee - ".
This helps distinguish the tests from Skript's tests in a GitHub action.
