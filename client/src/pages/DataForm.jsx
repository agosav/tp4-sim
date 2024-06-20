/* eslint-disable react/prop-types */
import React, {useState} from "react";
import {Button, Col, Form, Row} from "react-bootstrap";
import {Controller, useForm} from "react-hook-form";
import {colasServices} from "../services/colas.service";
import {useNavigate} from "react-router-dom";

const DataForm = ({setRespuestas}) => {
  const {
    register,
    handleSubmit,
    formState: {errors},
    reset,
    setValue,
    control,
    watch
  } = useForm({
    defaultValues: {
      porcentaje_aprendiz: 15.0,
      tiempo_atencion_min_aprendiz: 20.0,
      tiempo_atencion_max_aprendiz: 30.0,
      porcentaje_veterano_a: 45.0,
      complejidad_min: 3.0,
      complejidad_max: 10.0,
      tiempo_atencion_max_veterano_a: 13.0,
      porcentaje_veterano_b: 40.0,
      tiempo_atencion_min_veterano_b: 12.0,
      tiempo_atencion_max_veterano_b: 18.0,
      tiempo_llegada_min: 2.0,
      tiempo_llegada_max: 12.0,
      cantidad_dias: 100,
      hora_desde: 1,
      dia_desde: 1,
      cantidad_iteraciones: 10,
      h: 0.01,
      primer_num: 2.0,
      segundo_num: 0.5,
      tercer_num: 50.0
    },
  });

  const navigate = useNavigate();

  const tiempoAtencionMinAprendiz = parseFloat(watch("tiempo_atencion_min_aprendiz"));
  const tiempoAtencionMaxAprendiz = parseFloat(watch("tiempo_atencion_max_aprendiz"));
  const complejidadMin = parseFloat(watch("complejidad_min"));
  const complejidadMax = parseFloat(watch("complejidad_max"));
  const tiempoAtencionMinVeteranoB = parseFloat(watch("tiempo_atencion_min_veterano_b"));
  const tiempoAtencionMaxVeteranoB = parseFloat(watch("tiempo_atencion_max_veterano_b"));
  const tiempoLlegadaMin = parseFloat(watch("tiempo_llegada_min"));
  const tiempoLlegadaMax = parseFloat(watch("tiempo_llegada_max"));
  const cantidadDias = parseInt(watch("cantidad_dias"));
  const diaDesde = parseInt(watch("dia_desde"));
  const porcentajeAprendiz = parseFloat(watch("porcentaje_aprendiz"));
  const porcentajeVeteranoA = parseFloat(watch("porcentaje_veterano_a"));
  const porcentajeVeteranoB = parseFloat(watch("porcentaje_veterano_b"));

  const [errorAPI, setErrorApi] = useState("");

  const onSubmit = async (data) => {
    data.porcentaje_aprendiz = parseFloat(data.porcentaje_aprendiz);
    data.tiempo_atencion_min_aprendiz = parseFloat(
      data.tiempo_atencion_min_aprendiz
    );
    data.tiempo_atencion_max_aprendiz = parseFloat(
      data.tiempo_atencion_max_aprendiz
    );
    data.porcentaje_veterano_a = parseFloat(data.porcentaje_veterano_a);
    data.complejidad_min = parseFloat(data.complejidad_min);
    data.complejidad_max = parseFloat(data.complejidad_max)
    data.porcentaje_veterano_b = parseFloat(data.porcentaje_veterano_b);
    data.tiempo_atencion_min_veterano_b = parseFloat(
      data.tiempo_atencion_min_veterano_b
    );
    data.tiempo_atencion_max_veterano_b = parseFloat(
      data.tiempo_atencion_max_veterano_b
    );
    data.tiempo_llegada_min = parseFloat(data.tiempo_llegada_min);
    data.tiempo_llegada_max = parseFloat(data.tiempo_llegada_max);

    data.cantidad_dias = parseInt(data.cantidad_dias);
    data.hora_desde = parseInt(data.hora_desde);
    data.dia_desde = parseInt(data.dia_desde);
    data.cantidad_iteraciones = parseInt(data.cantidad_iteraciones);
    data.h = parseFloat(data.h);
    data.primer_num = parseFloat(data.primer_num);
    data.segundo_num = parseFloat(data.segundo_num);
    data.tercer_num = parseFloat(data.tercer_num);
    console.log(data);
    const response = await colasServices.simular(data);

    if (typeof response === "string") {
      setErrorApi(response);
      return;
    }
    setErrorApi("");

    console.log("rta de api: ", response);
    setRespuestas(response);
    navigate("/table");
  };

  return (
    <>
      <div className="card">
        <h3>Peluqueros</h3>

        <Row className="align-items-start">
          <Col>
            <h4 className="text-center">Aprendiz</h4>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Prob. de atender
                </Form.Label>
                <Controller
                  name="porcentaje_aprendiz"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^(\d{1,2}(\.\d*)?|100(\.0*)?)$/,
                      message: "Porcentaje inválido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.porcentaje_aprendiz && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.porcentaje_aprendiz.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_min_aprendiz"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    max: {
                      value: 480,
                      message: "Debe ser igual o menor a 480"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Minutos"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_min_aprendiz && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_min_aprendiz.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_max_aprendiz"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    max: {
                      value: 480,
                      message: "Debe ser igual o menor a 480"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Minutos"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_max_aprendiz && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_max_aprendiz.message}
                </span>
              )}
            </Form.Group>
            {tiempoAtencionMaxAprendiz <= tiempoAtencionMinAprendiz && (
              <span
                style={{
                  marginLeft: "140px",
                  color: "red",
                  fontWeight: "600",
                }}
              >
                      {"El mínimo debe ser menor al máximo."}
                  </span>
            )}
          </Col>


          <Col>
            <h4 className="text-center">Veterano A</h4>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Prob. de atender
                </Form.Label>
                <Controller
                  name="porcentaje_veterano_a"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^(\d{1,2}(\.\d*)?|100(\.0*)?)$/,
                      message: "Porcentaje inválido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.porcentaje_veterano_a && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.porcentaje_veterano_a.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Complejidad mínima
                </Form.Label>
                <Controller
                  name="complejidad_min"
                  control={control}
                  rules={{
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Complejidad Mínima"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.complejidad_min && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.complejidad_min.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Complejidad máxima
                </Form.Label>
                <Controller
                  name="complejidad_max"
                  control={control}
                  rules={{
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Complejidad Máxima"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.complejidad_max && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.complejidad_max.message}
                </span>
              )}
            </Form.Group>
            {complejidadMax <= complejidadMin && (
              <span
                style={{
                  marginLeft: "140px",
                  color: "red",
                  fontWeight: "600",
                }}
              >
                      {"El mínimo debe ser menor al máximo."}
                  </span>
            )}
          </Col>

          <Col>
            <h4 className="text-center">Veterano B</h4>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Prob. de atender
                </Form.Label>
                <Controller
                  name="porcentaje_veterano_b"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^(\d{1,2}(\.\d*)?|100(\.0*)?)$/,
                      message: "Porcentaje inválido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="%"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.porcentaje_veterano_b && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.porcentaje_veterano_b.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_min_veterano_b"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    max: {
                      value: 480,
                      message: "Debe ser igual o menor a 480"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Minutos"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_min_veterano_b && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_min_veterano_b.message}
                </span>
              )}
            </Form.Group>

            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_atencion_max_veterano_b"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    max: {
                      value: 480,
                      message: "Debe ser igual o menor a 480"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Minutos"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_atencion_max_veterano_b && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_atencion_max_veterano_b.message}
                </span>
              )}
            </Form.Group>
            {tiempoAtencionMaxVeteranoB <= tiempoAtencionMinVeteranoB && (
              <span
                style={{
                  marginLeft: "140px",
                  color: "red",
                  fontWeight: "600",
                }}
              >
                      {"El mínimo debe ser menor al máximo."}
                  </span>
            )}
          </Col>
        </Row>
        {porcentajeAprendiz + porcentajeVeteranoA + porcentajeVeteranoB !== 100 && (
          <span
            style={{
              color: "red",
              fontWeight: "600",
            }}
          >
                      {"La suma de las probabilidades no da 100%."}
                  </span>
        )}

        <hr/>
        <h3>Clientes</h3>
        <Row className="align-items-start">
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora mínima
                </Form.Label>
                <Controller
                  name="tiempo_llegada_min"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    max: {
                      value: 480,
                      message: "Debe ser igual o menor a 480"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Minutos"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_llegada_min && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_llegada_min.message}
                </span>
              )}
            </Form.Group>
          </Col>
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Demora máxima
                </Form.Label>
                <Controller
                  name="tiempo_llegada_max"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    max: {
                      value: 480,
                      message: "Debe ser igual o menor a 480"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Minutos"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tiempo_llegada_max && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tiempo_llegada_max.message}
                </span>
              )}
            </Form.Group>
          </Col>
          {tiempoLlegadaMax <= tiempoLlegadaMin && (
            <span
              style={{
                marginLeft: "140px",
                color: "red",
                fontWeight: "600",
              }}
            >
                      {"El mínimo debe ser menor al máximo."}
                  </span>
          )}
        </Row>

        <hr/>
        <h3>Simulación</h3>
        <Row className="align-items-start">
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Cantidad de días a simular
                </Form.Label>
                <Controller
                  name="cantidad_dias"
                  control={control}
                  rules={{
                    min: {
                      value: 1,
                      message: "El valor mínimo es 1"
                    },
                    pattern: {
                      value: /^[0-9]+$/,
                      message: "Ingrese un número entero válido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control type="number" {...field} placeholder="Ejemplo: 1000"/>
                  )}
                />
              </div>
              {errors.cantidad_dias && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.cantidad_dias.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Mostrar desde Hora
                </Form.Label>
                <Controller
                  name="hora_desde"
                  control={control}
                  rules={{
                    min: {
                      value: 1,
                      message: "El valor mínimo es 1"
                    },
                    max: {
                      value: 8,
                      message: "El valor máximo es 8",
                    },
                    pattern: {
                      value: /^[0-9]+$/,
                      message: "Ingrese un número entero válido",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control type="number" {...field} placeholder="Ejemplo: 1"/>
                  )}
                />
              </div>
              {errors.hora_desde && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.hora_desde.message}
                </span>
              )}
            </Form.Group>
          </Col>

          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Mostrar desde Día
                </Form.Label>
                <Controller
                  name="dia_desde"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^[0-9]+$/,
                      message: "Ingrese un número entero válido",
                    },
                    min: {
                      value: 1,
                      message: "El valor mínimo es 1"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control type="number" {...field} placeholder="Ejemplo: 1"/>
                  )}
                />
              </div>
              {errors.dia_desde && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.dia_desde.message}
                </span>
              )}
            </Form.Group>
            {diaDesde > cantidadDias && (
              <span
                style={{
                  marginLeft: "140px",
                  color: "red",
                  fontWeight: "600",
                }}
              >
                      {"El valor máximo es " + cantidadDias}
                  </span>
            )}
          </Col>

          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Cant. iteraciones
                </Form.Label>
                <Controller
                  name="cantidad_iteraciones"
                  control={control}
                  rules={{
                    pattern: {
                      value: /^[0-9]+$/,
                      message: "Ingrese un número entero válido",
                    },
                    max: {
                      value: 100000,
                      message: "El valor máximo es 100000",
                    },
                    min: {
                      value: 0,
                      message: "El valor mínimo es 0"
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control type="number" {...field} placeholder="Ejemplo: 100"/>
                  )}
                />
              </div>
              {errors.cantidad_iteraciones && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.cantidad_iteraciones.message}
                </span>
              )}
            </Form.Group>
          </Col>
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Paso (h)
                </Form.Label>
                <Controller
                  name="h"
                  control={control}
                  rules={{
                    min: {
                      value: 0,
                      message: "Debe ser igual o mayor a 0",
                    },
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Ingrese un número"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.h && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.h.message}
                </span>
              )}
            </Form.Group>
          </Col>
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Primer número
                </Form.Label>
                <Controller
                  name="primer_num"
                  control={control}
                  rules={{
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Ingrese un número"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.primer_num && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.primer_num.message}
                </span>
              )}
            </Form.Group>
          </Col>
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Segundo número
                </Form.Label>
                <Controller
                  name="segundo_num"
                  control={control}
                  rules={{
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Ingrese un número"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.segundo_num && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.segundo_num.message}
                </span>
              )}
            </Form.Group>
          </Col>
          <Col>
            <Form.Group controlId="1" style={{margin: "8px"}}>
              <div className="d-flex align-items-center">
                <Form.Label
                  className="mr-2"
                  style={{
                    marginRight: "12px",
                    width: "300px",
                    marginBottom: "0",
                  }}
                >
                  Tercer número
                </Form.Label>
                <Controller
                  name="tercer_num"
                  control={control}
                  rules={{
                    required: {
                      value: true,
                      message: "Este campo es requerido",
                    },
                  }}
                  render={({field}) => (
                    <Form.Control
                      type="number"
                      {...field}
                      placeholder="Ingrese un número"
                      step="0.1"
                    />
                  )}
                />
              </div>
              {errors.tercer_num && (
                <span
                  style={{
                    marginLeft: "140px",
                    color: "red",
                    fontWeight: "600",
                  }}
                >
                  {errors.tercer_num.message}
                </span>
              )}
            </Form.Group>
          </Col>
        </Row>

        <div className="align-itms-end">
          <Button className="btn btn-primary" onClick={handleSubmit(onSubmit)}
                  style={{"margin-top": "10px"}}>
            Calcular
          </Button>
        </div>
      </div>
    </>
  );
};

export {DataForm};
