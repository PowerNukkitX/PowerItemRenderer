# PowerItemRenderer  

PowerItemRenderer is a fast and lightweight item renderer for Minecraft.
It is classic raytracing (or sight-casting) based, but with minecraft-specific optimizations to make it faster.  

## Features  

- 0% code from mojang/microsoft is used or depended on, 100% open-source
- Platform and hardware independent, runs on any OS and any hardware, even if it has no GPU or OpenGL support
- Reasonable performance, it can render more than 100 items per second on a single core
- Native support for Minecraft's (Bedrock) item model format, no need to convert to other formats
- Out of the box support for [PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX) engine, but can be used with any other minecraft env
- [McMod](https://www.mcmod.cn/) support, you can export items to McMod format
- CI friendly, you can use it in your CI to generate images for your mods

## Project Structure

- CoreRenderer: The core renderer, it is a library that can be used to render models on any platform with CPU
- PNXItemRenderer: A renderer that uses the CoreRenderer to render items and blocks on [PowerNukkitX](https://github.com/PowerNukkitX/PowerNukkitX)

## License

PowerItemRenderer is licensed under the GNU GENERAL PUBLIC LICENSE Version 3.

## Author

- [Superice666](https://github.com/Superice666)
  - [McMod](https://center.mcmod.cn/250680/)
  - [MineBBS](https://www.minebbs.com/members/1854/)