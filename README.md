# mine-spawners
A Minecraft 1.20.x mod made with fabric that lets you mine spawners.
You can mine them with a silk touck pickaxe to get the spawner, doing so also makes the spawner not drop xp (for obvious reasons).

~~The mod reads a loot table from the json file at initialization, so spawners drop a spawner with the corresponding nbt data if mined with a silk touch pickaxe.~~
Not anymore, since 1.20.5, I haven't found a way to do it with a loot tables, since now you can only copy nbt into the item component "minecraft:custom_data". Workaround: it listens for the block break event and creates an item stack with the according nbt. Thank you Mojang, very cool.
There's a mixin to allow players to put the spawner without resetting its contents (this happened when placed on survival mode).
There's a second mixin to stop giving xp when broken with a silk touch pick.
And there's another mixin so the spawn nbt of the spawner is updated accordingly with the spawn egg used, when changing the contents of the spawner with one of them (See Issue #1).
