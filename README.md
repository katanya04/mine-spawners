# mine-spawners
A Minecraft 1.20-1.21 mod made with fabric that lets you mine spawners.
You can mine them with a silk touck pickaxe to get the spawner, doing so also makes the spawner not drop xp (for obvious reasons).

Before 1.20.5:
The mod reads a loot table from the json file at initialization, so spawners drop a spawner with the corresponding nbt data if mined with a silk touch pickaxe.
Before 1.21:
I haven't found a way to do it with loot tables, since now you can only copy nbt into the item component "minecraft:custom_data". Workaround: it listens for the block break event and creates an item stack with the according nbt. Thank you Mojang, very cool.
After 1.21:
It uses again a loot table (one for the spawner, the other for the trial spawner), that puts the nbt in the "minecraft:custom_data" component, so there's a mixin that moves the nbt from there to the correct path (the "minecraft:block_entity_data" component).

There's a mixin to allow players to put the spawner without resetting its contents (this happened when placed on survival mode).
There's a second mixin to stop giving xp when broken with a silk touch pick.
There's a third mixin to move the nbt to the correct place.
And there's another mixin so the spawn nbt of the spawner is updated accordingly with the spawn egg used, when changing the contents of the spawner with one of them (See Issue #1).
