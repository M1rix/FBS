package uz.apextech.fbs.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.apextech.fbs.web.rest.TestUtil;

class ImageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImageDTO.class);
        ImageDTO imageDTO1 = new ImageDTO();
        imageDTO1.setId(1L);
        ImageDTO imageDTO2 = new ImageDTO();
        assertThat(imageDTO1).isNotEqualTo(imageDTO2);
        imageDTO2.setId(imageDTO1.getId());
        assertThat(imageDTO1).isEqualTo(imageDTO2);
        imageDTO2.setId(2L);
        assertThat(imageDTO1).isNotEqualTo(imageDTO2);
        imageDTO1.setId(null);
        assertThat(imageDTO1).isNotEqualTo(imageDTO2);
    }
}
