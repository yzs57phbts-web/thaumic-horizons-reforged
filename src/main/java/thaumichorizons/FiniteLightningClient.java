package thaumichorizons;
final class FiniteLightningClient {
    static void draw(FiniteLightning entity){var random=entity.level().random;thaumcraft.client.fx.BoltRenderer.add(new thaumcraft.client.geom.LightningBolt(entity.getX()+random.nextFloat()-.5,entity.getY()+entity.length()+.5,entity.getZ()+random.nextFloat()-.5,entity.getX()+random.nextFloat()-.5,entity.getY()+1,entity.getZ()+random.nextFloat()-.5,random.nextLong(),6,.5F,8).defaultFractal().setType(6).setWidth(.1F));}
}
