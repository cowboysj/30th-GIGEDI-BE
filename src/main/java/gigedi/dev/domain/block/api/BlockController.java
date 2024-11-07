package gigedi.dev.domain.block.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gigedi.dev.domain.block.application.BlockService;
import gigedi.dev.domain.block.dto.request.BlockCreateRequest;
import gigedi.dev.domain.block.dto.request.BlockUpdateRequest;
import gigedi.dev.domain.block.dto.response.BlockCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Block", description = "Block 관련 API")
@RestController
@RequestMapping("/api/v1/block")
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;

    @Operation(summary = "블록 생성 API", description = "새로운 블록을 생성하는 API")
    @PostMapping("/{archiveId}")
    public BlockCreateResponse createBlock(
            @PathVariable Long archiveId, @RequestBody BlockCreateRequest request) {
        return blockService.createBlock(archiveId, request);
    }

    //    @Operation(summary = "아카이브 별 블록 조회 API", description = "아카이브 별 블록을 조회하는 API")
    //    @GetMapping("/{archiveId}")
    //    public BlockCreateResponse getBlock(@PathVariable Long archiveId) {
    //        return blockService.getBlock(archiveId);
    //    }

    @Operation(summary = "블록 제목 수정 API", description = "블록의 제목을 수정하는 API")
    @PatchMapping("/title/{blockId}")
    public BlockCreateResponse updateBlockTitle(
            @PathVariable Long blockId, @RequestBody BlockUpdateRequest request) {
        return blockService.updateBlockTitle(blockId, request);
    }

    @Operation(summary = "블록 삭제 API", description = "블록을 삭제하는 API")
    @DeleteMapping("/{blockId}")
    public void deleteBlock(@PathVariable Long blockId) {
        blockService.deleteBlock(blockId);
    }
}
