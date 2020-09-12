# Mining Gadgets Changelog
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## 1.16.3
### [1.6.0] - 2020-09-12
> note: Lots has been ported up from `1.16.1` since our first `1.16.2 ` release see:
> https://github.com/Direwolf20-MC/MiningGadgets/blob/master/CHANGELOG.md#142---2020-09-12 and
> https://github.com/Direwolf20-MC/MiningGadgets/blob/master/CHANGELOG.md#141---2020-08-21 for more info

#### Changed
- Ported to 1.16.3

## 1.16.2
### [1.5.1] - 2020-09-12
> note: Lots has been ported up from `1.16.1` since our first `1.16.2 ` release see:
> https://github.com/Direwolf20-MC/MiningGadgets/blob/master/CHANGELOG.md#142---2020-09-12 and
> https://github.com/Direwolf20-MC/MiningGadgets/blob/master/CHANGELOG.md#141---2020-08-21 for more info

#### Changed
- Re-ported to 1.16.2... :+1:

### [1.5.0] - 2020-08-14

#### Changed
- Updated to 1.16 :+1:

## 1.16.1
### [1.4.2] - 2020-09-12
#### Added
- A new render for when the Item is in your hand thanks to [ItsTheBdoge](https://github.com/ItsTheBdoge) :heart:
- Added `hold shift` for full energy reading aka: `1M/1M FE` to `1,000,000/1,000,000 FE` It's nice! :D
- Updated Modification table textures a bit

#### Changed
- You can now use right click on TileEntities with the Gadget in your hand again. The blocker on this operation has been modified to only apply to things you might have in your offhand like torches. It's not perfect, but the perfect option requires a PR to Forge...

#### Fixed
- Fixed the Mining Gadget going into negative energy due to a issue with freezing upgrade 

### [1.4.1] - 2020-08-21
#### Added
- Added a (disabled by default) key binding to open the Gadgets settings screen. (When enabled, it will disable the shift right click action)
- Added a Laser Beam preview to the Mining Gadget Visual Screen
- Added `Paver` upgrade which will place cobble (cost of 10FE per block) to create a path as you mine to cover up any holes. It will not place above your player's height, and we plan on changing it from cobble :D 
- Modification table can now have upgrades dropped into it's upgrade area (pick up and drop down)
- Modification table now has an `empty state` to show helper text on how to use the table.
- Modification table now has a title :eyes:

#### Changed
- Improved the look of the Mining Gadget Visual Screen
- The modification table will now show the full tooltip for you upgrades
- Bumped forge version

#### Fixed

- [#98] Config changes did not affect upgrade costs.
- Fixed client crash on shift clicking items to the Modification Table
- Fixed laser rendering through walls when another player is using the gadget

### [1.4.0] - 2020-08-03
#### Changed
- Updated the mod to 1.16.1 :+1:

## 1.15.2
### [1.3.5] - 2020-03-29
#### Added
- Support for all (if they respect events) items that pick up items (dank storage, pocket storage, etc)
- Added the `E` key as a way to get out the Mining Gadget shift-click screen 
- Added support for both the Silk touch and Fortune upgrades to be applied to the same gadget but only one can be active at a time
- Added support for break ice blocks and placing water back in it's place (like vanilla does)
- Added support for spawning additional drops (silver fish and others)
- Added the ability to reduce freeze particles by adding a slight delay per tick (configurable in the gadget menu) (You're welcome Soaryn)
- Added a charged variant of the gadget to the creative and JEI (1.3.3)
- Added RU translations (Thanks to @Smollet777)

#### Changed
- Disabled the default overlay when using the gadget to stop showing two highlights at the same time
- There is no longer a middle man slot for upgrades in the upgrade table. You can now shift click upgrades right into the gadget and click the upgrade to remove it
- Slightly improved the light particle
- The Mining Gadget can now ray trace through non-collidable blocks meaning it can now shoot though the Miners Light!
- The Mining Gadget now adds to your break block stats :D

#### Fixed
- Fixed Mining gadget being voided when left in a Modification table and breaking the table
- Fixed an issue with OneProbe on RenderBlock look at
- Fixed typo in tooltips
- Pushed the laser render to the right spot when running
- We now respect the break block event being cancelled
- Fixed voiding upgrades when normal clicking them in the modification table.
- Allowing the Gadget to be used for infinite energy (1.3.3) 

### [1.3.1] - 2020-03-08
#### Fixed
- Beta release now. Fixed a few bugs reporting in 1.3.0

### [1.3.0] - 2020-03-03
#### Added
- The first release for 1.15.2 - I think its pretty stable? :)

## 1.14.4
### [1.2.4]
#### Added
- Added `ru` translations thanks to @Smollet777
- You can now use the scroll wheel to change the sliders :D
- You can now shift click into the filter

#### Fixed
- Fixed the filters not working #42
- Fixed blacklist filtering system
- Added checking to make sure the dimention also allows for block breaking
- Partialy fixed an odd energy bug
- Fixed edgecase crashing on the render block

#### Changed
- Optimised images and json files for a smaller mod build size
- Took the time to bump Forge up to `28.2.4`

### [1.2.3]
#### Added
- Added Precision Mode (A Soaryn request!)

### [1.2.2]
#### Fixed
- Fixed a dupe bug - Thanks Soaryn.

### [1.2.1]
#### Added
- Added a tooltip to the upgrades and gadget to show RF costs.

### [1.2.0] - ?
#### Added
- The whole mod!

# Example
## [MC-Verion, VERSION] - Date of release
### Added
- 
### Changed
- 
### Deprecated
- 
### Removed
- 
### Fixed
- 
### Security
- 
