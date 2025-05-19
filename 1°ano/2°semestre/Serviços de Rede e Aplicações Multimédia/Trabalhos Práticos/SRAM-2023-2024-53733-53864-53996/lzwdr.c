/**
 * SERVIÇOS DE REDE E APLICAÇÕES MULTIMÉDIA 2024
 *
 * @name LZWdR
 *
 *
 * @date 12/06/24
 *
 * @authors Catarina Pereira  <pg53733@alunos.uminho.pt>
 *          Inês Neves <pg53864@alunos.uminho.pt>
 *          Leonardo Martins <pg53996@alunos.uminho.pt>
 *
 * @details Este programa tem como objetivo codificar um ficheiro input
 *          e escrever os outputs da codificação num ficheiro output
 *          através da codificação experminetal LZWdR
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>
#include <ctype.h>

#define comprimento_buffer 10
#define MAX_PATTERNS 1000
#define tam_dic_20 1048576
#define tam_dic_18 262144
//ficheiros
FILE *ent;
FILE *out;

//blocos
unsigned long long tam_max_dicionario = 65536; //Default dictionary(trie) size
long int t_b=0;
unsigned int quant_dici = 0;
long unsigned int tam_blocos = 65536;
unsigned long long total;
int n_blocks;
unsigned long size_of_last_block;
int pos_bloco=0;
unsigned char **buffer_of_file;
int pos_bufferfile = 0;
char* output_file;
char* input_file;
int dict_clear = 0;
int dict_size = 0;
const char *cleanup_messages[] = {
    "Sem Limpeza",   // Index 0
    "Total",  // Index 1
    "Parcial"     // Index 2
};

int last_position_of_dici = 0;

// Trie Node
typedef struct TrieNode {
    struct TrieNode *children[256];
    int index;
    bool isEndOfPattern;
    int usageCount;
} TrieNode;

TrieNode *root;

// output buffer
int output[200000000];

// Function to create a new Trie node
TrieNode *createNode() {
    TrieNode *node = (TrieNode *)malloc(sizeof(TrieNode));
    if (node == NULL) {
        perror("Memory allocation failed at TrieNode");
        exit(EXIT_FAILURE);
    }
    
    node->isEndOfPattern = false;
    node->index = -1;
    node->usageCount = 0;
    for (int i = 0; i < 256; i++) {
        node->children[i] = NULL;
    }
    return node;
}

// Initialize the Trie
void initTrie() {
    root = createNode();
    // Add single character patterns to the Trie
    for (int i = 0; i < 256; i++) {
        TrieNode *node = root;
        node->children[i] = createNode();
        node = node->children[i];
        node->isEndOfPattern = true;
        node->index = i;
    }
    last_position_of_dici = 256;
}

// Function to insert a pattern into the Trie
void insertPattern(unsigned char *pattern, int length, int index) {
    TrieNode *node = root;
    for (int i = 0; i < length; i++) {
        int idx = pattern[i];
        if (node->children[idx] == NULL) {
            node->children[idx] = createNode();
        }
        node = node->children[idx];
    }
    node->isEndOfPattern = true;
    node->index = index;
    node->usageCount = 0;
}

// Function to check if a pattern exists in the Trie
bool check_if_in_dici(unsigned char *pattern, int length) {
    TrieNode *node = root;
    for (int i = 0; i < length; i++) {
        int idx = pattern[i];
        if (node->children[idx] == NULL) {
            return false;
        }
        node = node->children[idx];
    }
    return node->isEndOfPattern;
}

// Function to get the index of a pattern in the Trie
int output_number(unsigned char *pattern, int length) {
    TrieNode *node = root;
    for (int i = 0; i < length; i++) {
        int idx = pattern[i];
        if (node->children[idx] == NULL) {
            return -1;  // Pattern not found
        }
        node = node->children[idx];
    }
    node->usageCount++;
    return node->index;
}

// Function to insert a pattern into the dictionary
void insert_value_dici(unsigned char *pa, unsigned char *pb, int tam_pa, int tam_pb) {
    int tam_padrao_maior = tam_pa + tam_pb;
    int pos_tam = tam_pa + 1;
    unsigned char aux[64];
    for (int i = 0; i < tam_pa; i++) {
        aux[i] = pa[i];
    }
    int pos_pb = 0;
    while (tam_padrao_maior >= pos_tam) {
        if (pos_tam <= comprimento_buffer) {
            aux[pos_tam - 1] = pb[pos_pb];
            if (!check_if_in_dici(aux, pos_tam) && last_position_of_dici < tam_max_dicionario) {
                insertPattern(aux, pos_tam, last_position_of_dici);
                pos_tam++;
                pos_pb++;
                last_position_of_dici += 1;
            } else {
                pos_pb++;
                pos_tam++;
            }
        } else {
            pos_tam += tam_padrao_maior;
        }
    }
}

// Function to pick the next pattern
void pick_PB(unsigned char *pb, int *t) {
    unsigned char aux[64];
    aux[0] = buffer_of_file[pos_bloco][pos_bufferfile + 1];
    pos_bufferfile++;
    int tamanho = 1;
    bool check = true;
    while (check) {
        if (check_if_in_dici(aux, tamanho)) {
            pb[tamanho - 1] = buffer_of_file[pos_bloco][pos_bufferfile];
            pos_bufferfile++;
            aux[tamanho] = buffer_of_file[pos_bloco][pos_bufferfile];
            tamanho++;
            if (pos_bufferfile > tam_blocos - 1) {
                tamanho--;
                pos_bufferfile--;
                check = false;
                *t = tamanho;
            }
        } else {
            tamanho--;
            pos_bufferfile--;
            *t = tamanho;
            check = false;
        }
    }
}

// Function to read blocks
int leitura_blocos() {
    if (ent == NULL) {
        perror("Error opening file");
        return 1;
    }

    fseek(ent, 0, SEEK_END); // Move the file pointer to the end of the file
    total = ftell(ent); // Get the current position of the file pointer (which is the size of the file)
    rewind(ent);
    
    if (total % tam_blocos == 0){
        n_blocks = (int) total / tam_blocos;
        size_of_last_block = tam_blocos;
    }else{
        n_blocks = (int) (total / tam_blocos) + 1;
        size_of_last_block = total % tam_blocos;
    }

    if (n_blocks == 1) {
        tam_blocos = size_of_last_block;
        t_b = size_of_last_block;
    } else {
        t_b = tam_blocos;
    }

    return n_blocks, total, size_of_last_block;
}

// Function to fill blocks
int fill_blocos() {
    buffer_of_file = malloc(n_blocks * sizeof(char *));
    if (buffer_of_file == NULL) {
        perror("Erro ao alocar memória 1");
        exit(EXIT_FAILURE);
    }
    for (int i = 0; i < n_blocks; i++) {
        buffer_of_file[i] = malloc(tam_blocos * sizeof(char));
        if (buffer_of_file[i] == NULL) {
            perror("Erro ao alocar memória 2");
            exit(EXIT_FAILURE);
        }
    }
    size_t bt = 0;
    for (int i = 0; i < n_blocks; i++) {
        bt = fread(buffer_of_file[i], 1, tam_blocos, ent);
        if (bt < tam_blocos && !feof(ent)) {
            perror("Erro de leitura do ficheiro");
            exit(EXIT_FAILURE);
        }
    }
    fclose(ent);
}

// Function to create the dictionary
int criar_dicionario() {
    initTrie();
}

// Function to free the Trie
void freeTrie(TrieNode *node) {
    for (int i = 0; i < 256; i++) {
        if (node->children[i] != NULL) {
            freeTrie(node->children[i]);
        }
    }
    free(node);
}

// Struct to hold pattern information
typedef struct {
    unsigned char pattern[comprimento_buffer];
    int length;
    int index;
    int usageCount;
} Pattern;

// Comparator function for sorting patterns by usage count
int comparePatterns(const void *a, const void *b) {
    Pattern *patternA = (Pattern *)a;
    Pattern *patternB = (Pattern *)b;
    return patternB->usageCount - patternA->usageCount;
}

// Function to collect the top 10 patterns
void collectTopPatterns(Pattern *topPatterns, int *topPatternCount) {
    int patternIndex = 0;
    for (int i = 0; i < 256; i++) {
        TrieNode *node = root->children[i];
        if (node != NULL && node->isEndOfPattern && node->usageCount > 0) {
            if (patternIndex < MAX_PATTERNS) {
                topPatterns[patternIndex].pattern[0] = (unsigned char)i;
                topPatterns[patternIndex].length = 1;
                topPatterns[patternIndex].index = node->index;
                topPatterns[patternIndex].usageCount = node->usageCount;
                patternIndex++;
            }
        }
    }
    *topPatternCount = patternIndex;
    qsort(topPatterns, patternIndex, sizeof(Pattern), comparePatterns);
}

// Function to reset the dictionary and retain the top patterns
void reset_dictionary() {
    if(dict_clear == 2){ //Partial Cleanup
        Pattern topPatterns[MAX_PATTERNS];
        int topPatternCount = 0;
    
        collectTopPatterns(topPatterns, &topPatternCount);

        freeTrie(root);
        initTrie();

        for (int i = 0; i < topPatternCount; i++) {
            insertPattern(topPatterns[i].pattern, topPatterns[i].length, topPatterns[i].index);
        }
        last_position_of_dici = 256 + topPatternCount;
        quant_dici += 1;
    }else if(dict_clear == 1){ //Total Cleanup
        freeTrie(root);
        initTrie();
        quant_dici += 1;
    }
    
}

void printHelp() {
    printf("Usage: program [OPTIONS]\n");
    printf("Options:\n");
    printf("  -h, --help              Show this help message and exit\n");
    printf("  -i, --input             Specify the input file\n");
    printf("  -o, --output            Specify the output file\n");
    printf("  -c, --cleanup 0|1|2     Specify the cleanup option (0, 1, or 2)\n");
    printf("      (0-> No cleanup, 1 -> Full cleanup, 2-> Partial cleanup)\n");
    printf("  -d, --dictionary 0|1|2  Specify the dictionary size option (0, 1, or 2)\n");
    printf("      (0-> 2^16 , 1 -> 2^18, 2-> 2^20)\n");
}

int isValidInteger(const char *str) {
    if (*str == '-' || *str == '+') {
        str++;
    }
    if (*str == '\0') {
        return 0;
    }
    while (*str) {
        if (!isdigit(*str)) {
            return 0;
        }
        str++;
    }
    return 1;
}

int main(int argc, char *argv[]) {
    
    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "-h") == 0 || strcmp(argv[i], "--help") == 0) {
            printHelp();
            return 0;
        } else if (strcmp(argv[i], "-i") == 0 || strcmp(argv[i], "--input") == 0) {
            if (i + 1 < argc){
                input_file = argv[++i];
                ent = fopen(input_file, "rb");
                if (ent == NULL) {
                    perror("Error opening file");
                    exit(EXIT_FAILURE); // Exit with a failure status
                }
            }else{
                fprintf(stderr, "Error: -i or --input option requires a filename\n");
                return 1;
            }
        } else if (strcmp(argv[i], "-o") == 0 || strcmp(argv[i], "--output") == 0) {
            if (i + 1 < argc) {
                output_file = argv[++i];
            } else {
                fprintf(stderr, "Error: -o or --output option requires a filename\n");
                return 1;
            }
        } else if (strcmp(argv[i], "-c") == 0 || strcmp(argv[i], "--cleanup") == 0) {
            if (i + 1 < argc) {
                if (isValidInteger(argv[i + 1])) {
                    dict_clear = atoi(argv[++i]);
                    if (dict_clear < 0 || dict_clear > 2) {
                        fprintf(stderr, "Error: -c or --cleanup option requires a value of 0, 1, or 2\n");
                        return 1;
                    }
                } else {
                    fprintf(stderr, "Error: -c or --cleanup option requires a numeric value\n");
                    return 1;
                }
            }else{
                fprintf(stderr, "Error: -c or --cleanup option requires a value of 0, 1, or 2\n");
                return 1;
            }
        }else if (strcmp(argv[i], "-d") == 0 || strcmp(argv[i], "--dictionary") == 0) {
            if (i + 1 < argc) {
                if (isValidInteger(argv[i + 1])) {
                    dict_size = atoi(argv[++i]);
                    if (dict_size < 0 || dict_size > 2) {
                        fprintf(stderr, "Error: -c or --cleanup option requires a value of 0, 1, or 2\n");
                        return 1;
                    }else if(dict_size == 1){
                        tam_max_dicionario = tam_dic_18;
                    }else if(dict_size == 2){
                        tam_max_dicionario = tam_dic_20;
                    }
                } else {
                    fprintf(stderr, "Error: -c or --cleanup option requires a numeric value\n");
                    return 1;
                }
            }else{
                fprintf(stderr, "Error: -c or --cleanup option requires a value of 0, 1, or 2\n");
                return 1;
            }
        } else {
            fprintf(stderr, "Unknown option: %s\n", argv[i]);
            return 1;
        }
    }

    if(ent == NULL){
        fprintf(stderr, "Error: No input file specified\nFor help do ./lzwdr.exe -h");
        exit(EXIT_FAILURE);
    }
    if(output_file == NULL){
        output_file = "saida.lzwdr"; //Default Output file in case of no output file selected
    }
    
    


    printf("Autores: Catarina Pereira PG53733, Ines Neves PG53864 e Leonardo Martins PG53996\n");
    printf("Data de criacao: 30/05/2024 - 12/06/2024\n");
    printf("Ficheiro de entrada: %s\n", input_file);
    printf("Ficheiro de Saida: %s\n", output_file);
    printf("Tamanho maximo do dicionario: %d\n", tam_max_dicionario);
    printf("Tipo de dicionario inicial: 256\n");
    printf("Tipo de limpeza de dicionario: %s.\n", cleanup_messages[dict_clear]);
    printf("-----------------------------------------\n");

    clock_t begin = clock();
    clock_t block_time = clock();
    criar_dicionario();
    leitura_blocos();
    fill_blocos();
    fclose(ent);

    unsigned char pa[64];
    unsigned char pb[64];
    last_position_of_dici = 256;
    pos_bufferfile = 1;
    pa[0] = buffer_of_file[pos_bloco][0];
    pb[0] = buffer_of_file[pos_bloco][1];
    int size_pb = 1;
    int size_pa = 1;

    insert_value_dici(pa, pb, 1, 1);
    int incr_out = 0;
    output[incr_out] = output_number(pa, size_pa) + 1;
    pa[0] = pb[0];
    int inc = 0;
    incr_out++;
    while (n_blocks > pos_bloco) {
        pick_PB(pb, &size_pb);
        if (last_position_of_dici >= tam_max_dicionario && dict_clear != 0) {
            reset_dictionary();
        }
        insert_value_dici(pa, pb, size_pa, size_pb);
        output[incr_out] = output_number(pa, size_pa) + 1;
        incr_out++;
        for (int i = 0; i < size_pb; i++) {
            pa[i] = pb[i];
        }
        size_pa = size_pb;
        if (tam_blocos - 1 <= pos_bufferfile) {
            output[incr_out] = output_number(pa, size_pa) + 1;
            incr_out++;
            pos_bufferfile = 0;
            pos_bloco++;
            if (pos_bloco == n_blocks - 1) {
                tam_blocos = size_of_last_block;
            }
            if (n_blocks != pos_bloco) {
                pa[0] = buffer_of_file[pos_bloco][0];
                size_pa = 1;
            }
            clock_t end_1 = clock();
            printf("Tempo de execucao bloco %d - %d -> %.6f segundos\n", pos_bloco, n_blocks ,(double)(end_1 - block_time) / CLOCKS_PER_SEC);
            block_time = end_1;
        }
    }

    out = fopen(output_file, "wb");
    if (out == NULL) {
        perror("Failed to open output file");
        exit(EXIT_FAILURE);
    }
    
    for (int y = 0; y < incr_out; y++) {
        fwrite(&output[y], 2, 1, out);
    }
    fclose(out);
    clock_t end = clock();
    printf("\n");
    double tempo = (double)(end - begin) / CLOCKS_PER_SEC;
    printf("Tempo de execucao final: %.4f segundos\n", tempo);
    printf("Numero de padroes no dicionario: %d\n", last_position_of_dici);
    printf("Quantidade de dicionarios cheios: %d\n", quant_dici);
    printf("Tamanho do ficheiro: %lld bytes\n", total);
    printf("Numero de blocos : %d\n", n_blocks);
    printf("Tamanho dos blocos: %d bytes\n", t_b);
    printf("Tamnho do ultimo bloco: %d bytes\n", size_of_last_block);
    printf("Tamanho do ficheiro de saida: %d bytes\n", incr_out * 2);

    int num_file = (total - (incr_out * 2));
    double taxa_de_compressao = (double)num_file / total;
    printf("Taxa de compressao: %.2f%%\n", taxa_de_compressao * 100);

    FILE *out_visivel = fopen("output_visivel.txt", "wb");
    if (out_visivel == NULL) {
        perror("Failed to open visible output file");
        exit(EXIT_FAILURE);
    }
    for (int y = 0; y < incr_out; y++) {
        fprintf(out_visivel, "%u", output[y]);
        if (y != incr_out - 1) {
            fprintf(out_visivel, ",");
        }
    }
    fclose(out_visivel);
    for (int i = 0; i < n_blocks; i++) {
        free(buffer_of_file[i]);
    }
    free(buffer_of_file);
    freeTrie(root);
    return 0;
}
