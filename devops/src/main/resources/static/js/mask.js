// Função para aplicar máscara de telefone (XX) 9 XXXX-XXXX
function maskPhone(value) {
    if (!value) return '';
    value = value.replace(/\D/g, '');
    if (value.length > 11) value = value.slice(0, 11);
    
    if (value.length <= 2) {
        return value;
    } else if (value.length <= 6) {
        return `(${value.slice(0, 2)}) ${value.slice(2)}`;
    } else if (value.length <= 10) {
        return `(${value.slice(0, 2)}) ${value.slice(2, 6)}-${value.slice(6)}`;
    } else {
        return `(${value.slice(0, 2)}) ${value.slice(2, 7)}-${value.slice(7)}`;
    }
}

// Função para aplicar máscara de RM (XXXXX-X)
function maskRM(value) {
    if (!value) return '';
    value = value.replace(/\D/g, '');
    if (value.length > 6) value = value.slice(0, 6);
    
    if (value.length <= 5) {
        return value;
    } else {
        return `${value.slice(0, 5)}-${value.slice(5)}`;
    }
}

// Função para aplicar máscara de CPF (XXX.XXX.XXX-XX)
function maskCPF(value) {
    if (!value) return '';
    value = value.replace(/\D/g, '');
    if (value.length > 11) value = value.slice(0, 11);
    
    if (value.length <= 3) {
        return value;
    } else if (value.length <= 6) {
        return `${value.slice(0, 3)}.${value.slice(3)}`;
    } else if (value.length <= 9) {
        return `${value.slice(0, 3)}.${value.slice(3, 6)}.${value.slice(6)}`;
    } else {
        return `${value.slice(0, 3)}.${value.slice(3, 6)}.${value.slice(6, 9)}-${value.slice(9)}`;
    }
}

// Função para aplicar máscara de CEP (XXXXX-XXX)
function maskCEP(value) {
    if (!value) return '';
    value = value.replace(/\D/g, '');
    if (value.length > 8) value = value.slice(0, 8);
    
    if (value.length <= 5) {
        return value;
    } else {
        return `${value.slice(0, 5)}-${value.slice(5)}`;
    }
}

// Função para aplicar máscara de números apenas
function maskNumbers(value) {
    if (!value) return '';
    return value.replace(/\D/g, '');
}

// Inicializar máscaras ao carregar o documento
document.addEventListener('DOMContentLoaded', function() {
    // Máscaras de telefone
    const phoneFields = document.querySelectorAll('[data-mask="phone"]');
    phoneFields.forEach(field => {
        field.addEventListener('input', function() {
            this.value = maskPhone(this.value);
        });
    });

    // Máscaras de RM
    const rmFields = document.querySelectorAll('[data-mask="rm"]');
    rmFields.forEach(field => {
        field.addEventListener('input', function() {
            this.value = maskRM(this.value);
        });
    });

    // Máscaras de CPF
    const cpfFields = document.querySelectorAll('[data-mask="cpf"]');
    cpfFields.forEach(field => {
        field.addEventListener('input', function() {
            this.value = maskCPF(this.value);
        });
    });

    // Máscaras de CEP
    const cepFields = document.querySelectorAll('[data-mask="cep"]');
    cepFields.forEach(field => {
        field.addEventListener('input', function() {
            this.value = maskCEP(this.value);
        });
    });

    // Máscaras de números
    const numberFields = document.querySelectorAll('[data-mask="number"]');
    numberFields.forEach(field => {
        field.addEventListener('input', function() {
            this.value = maskNumbers(this.value);
        });
    });
});
