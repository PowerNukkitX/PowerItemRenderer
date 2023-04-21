# PNXItemRenderer  

## Usage  

```
/render <namespace: string> [manifest|image|mcmod] <renderConfig: file_path>
```

- `namespace`: The namespace or modId of the item, it is used to find the item model and textures
- `manifest|image|mcmod`: The output format, it can be `manifest`, `image` or `mcmod`. If it's not specified, it will be `manifest`
- `renderConfig`: The path to the render config file, it can be a relative path or an absolute path

## Config  

```json5
{
  "texturePackPath": "resource_packs/xxx.mcpack", // The path to the texture pack, it can be a relative path or an absolute path
  "outputPathDir": "plugins/PNXItemRenderer/xxx", // The path to the output directory, it can be a relative path or an absolute path
  "renderingTaskList": { // The rendering task list, this field is optional, PIR will complete the rest of the tasks automatically
    "xxx:my_item": {
      "namespaceId": "xxx:my_item", // Do not change this field, it is used to identify the item and should be the same as the key
      "texturePackPath": "resource_packs/xxx.mcpack", // You can override the texture pack path for this task
      "inPackTexturePath": { // The path to the textures in the texture pack, it is a relative path in the zipped texture pack file
        "up": "textures/items/xxx.png",
        "down": "textures/items/xxx.png",
        "north": "textures/items/xxx.png",
        "south": "textures/items/xxx.png",
        "west": "textures/items/xxx.png",
        "east": "textures/items/xxx.png",
        "any": "textures/items/xxx.png" // If the texture is not found in the other directions, it will use this texture
      },
      "permutationIndex": 0, // The permutation index of the item, it is used to render the block in different states
      "isSingleSide": true, // Whether the item is a single side item, it is used to render transparent blocks
      "ambientLight": 0.1, // The ambient light of the item, it is a float value between 0 and 1
      "data": {} // extra data that will be copied into the output file
    }
  }
}
```

Please note that only the `texturePackPath` and `outputPathDir` fields are required, other fields are optional.  

